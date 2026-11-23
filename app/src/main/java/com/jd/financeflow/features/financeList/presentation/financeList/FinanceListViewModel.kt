package com.jd.financeflow.features.financeList.presentation.financeList

import java.time.LocalDate

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.core.domain.models.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.models.financeFilter.FilterConfiguration
import com.jd.financeflow.features.core.domain.models.finance.Finance
import com.jd.financeflow.features.core.domain.models.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.domain.models.finance.IncomeCategory
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.annualCashFlow
import com.jd.financeflow.features.financeList.domain.FinanceListRepository


/**
 * Finance list view model.
 *
 * @param repository Finance repository
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 * @param filterConfiguration Filter configuration
 */
class FinanceListViewModel(
    private val repository: FinanceListRepository,
    private val currencyCode: String = "EUR",
    private val filterConfiguration: FilterConfiguration? = null
) : ViewModel() {

    // Full list of last cached finances from local storage
    private var cachedAllFinances: List<Finance> = emptyList()

    // Filtered list of finances using provided filter configuration
    private var cachedFilteredFinances: List<Finance> = emptyList()

    // Job for debouncing search query
    private var searchJob: Job? = null

    private val _state = MutableStateFlow(
        FinanceListState(
            currencyCode = currencyCode,
            filterConfiguration = filterConfiguration
        )
    )
    val state: StateFlow<FinanceListState> = _state
        .onStart {
            refreshFinanceList(refresh = true)
            observeSearchQuery()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    /**
     * Handles actions for the FinanceList screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: FinanceListActions) {
        when (action) {
            is FinanceListActions.OnClearError -> _state.update { it.copy(error = null) }
            is FinanceListActions.OnAddFinanceClicked -> {}
            is FinanceListActions.OnFilterClicked -> {}
            is FinanceListActions.OnFinanceItemClicked -> {}
            is FinanceListActions.OnEnableSearch -> _state.update { it.copy(isSearching = action.value) }
            is FinanceListActions.OnUpdateSearchQuery -> _state.update { it.copy(searchQuery = action.value) }
            is FinanceListActions.OnRefresh -> refreshFinanceList(
                refresh = true,
                query = state.value.searchQuery,
            )

            is FinanceListActions.OnSelectedDeleteFinanceItem -> _state.update {
                it.copy(
                    showDeleteDialog = action.showDialog,
                    selectedDeleteFinance = action.finance
                )
            }

            is FinanceListActions.OnFinanceItemDelete -> deleteFinance(action.finance)
        }
    }

    /**
     * Observes the search query and filters the list accordingly.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L.milliseconds)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update { it.copy(finances = cachedFilteredFinances) }
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = refreshFinanceList(query = query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Refreshes the list of finances.
     *
     * @param refresh whether to get new list form local storage or use current list. Only one
     * search field can be used, [query] or [filterConfiguration]. Prior to option has precedence.
     * @param query the query to be used for filtering
     */
    private fun refreshFinanceList(
        refresh: Boolean = false,
        query: String? = null
    ) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        try {
            var balance: Double

            // Refresh cached finances if required. this allows us to respond faster to refresh and
            // query operations.
            if (refresh) {
                // Get all finances from local storage
                when (val result = repository.getFinances()) {
                    is Result.Success -> cachedAllFinances = result.data.sortedByDescending { it.timeStampMs }

                    is Result.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = result.error.toUiText())
                        }
                        return@launch
                    }
                }

                // Start tasks in parallel on background threads. The user selected amount range
                // is used as-is, it must not be rescaled/rounded, otherwise items get dropped.
                val filteredFinancesDeferred = async(Dispatchers.Default) {
                    filterFinances(cachedAllFinances, filterConfiguration)
                }
                val balanceDeferred = async(Dispatchers.Default) {
                    calculateBalance(cachedAllFinances)
                }

                cachedFilteredFinances = filteredFinancesDeferred.await()
                balance = balanceDeferred.await()

                // Update amount range limits for the filter slider. Limits are derived from ALL
                // finances (absolute values, so expenses count as positive) so they don't shrink
                // after a filter is applied. Upper limit gets 10% headroom rounded up to 1000.
                val currentConfig = state.value.filterConfiguration ?: FilterConfiguration()
                val highestAmount = cachedAllFinances.maxOfOrNull { item -> abs(item.amount) } ?: 0.0
                val roundedLimit = ceil(highestAmount * 1.1 / 1000.0) * 1000.0
                val maxAmountLimit = maxOf(roundedLimit, currentConfig.maxSetAmount ?: 0.0)
                _state.update {
                    it.copy(
                        filterConfiguration = currentConfig.copy(
                            maxAmountLimit = maxAmountLimit,
                            miniAmountLimit = 0.0
                        )
                    )
                }
            } else {
                balance = calculateBalance(cachedAllFinances)
            }

            // Filter by query if provided on background thread
            val queriedFinances = withContext(Dispatchers.Default) {
                cachedFilteredFinances.filter { item ->
                    item.title.contains(query ?: "", ignoreCase = true) ||
                            item.description?.contains(query ?: "", ignoreCase = true) == true
                }
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    finances = queriedFinances,
                    currentBalance = balance
                )
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(isLoading = false, error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Deletes the provided finance item.
     *
     * @param finance finance item to be deleted
     */
    private fun deleteFinance(finance: Finance) = viewModelScope.launch {
        val currentState = state.value

        when (val result = repository.deleteFinance(finance)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = false,
                        selectedDeleteFinance = null
                    )
                }
                refreshFinanceList(refresh = true, query = currentState.searchQuery)
            }

            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        selectedDeleteFinance = null,
                        error = result.error.toUiText()
                    )
                }
            }
        }
    }

    /**
     * Filters the finances based on the provided configuration.
     *
     * @param finances the list of finances to be filtered
     * @param configuration the filter configuration to be used for filtering
     * @return the filtered list of finances
     */
    private suspend fun filterFinances(
        finances: List<Finance>,
        configuration: FilterConfiguration?
    ): List<Finance> = withContext(Dispatchers.Default) {
        finances
            .filter { item ->
                // Filter by finance type
                val financeTypeCheck = when (configuration?.type) {
                    FinanceType.INCOME -> item.category is FinanceCategory.Income
                    FinanceType.EXPENSE -> item.category is FinanceCategory.Expense
                    null -> true
                }

                // Filter by category
                val categoryCheck =
                    if (configuration?.enableCategoryFilter == true && configuration.type != null) {
                        val categoryCheckList = mutableListOf<FinanceCategory>()

                        // Add all selected categories to comparison/check list
                        when (configuration.type) {
                            FinanceType.INCOME -> {
                                IncomeCategory.entries.forEachIndexed { index, category ->
                                    if (configuration.selectedIncomeIndexes?.get(index) == true) {
                                        categoryCheckList.add(FinanceCategory.Income(category))
                                    }
                                }
                            }

                            FinanceType.EXPENSE -> {
                                ExpenseCategory.entries.forEachIndexed { index, category ->
                                    if (configuration.selectedExpenseIndexes?.get(index) == true) {
                                        categoryCheckList.add(FinanceCategory.Expense(category))
                                    }
                                }
                            }
                        }

                        // Check if finance category is in comparison/check list
                        if (categoryCheckList.isNotEmpty()) {
                            categoryCheckList.contains(item.category)
                        } else {
                            true
                        }
                    } else {
                        true
                    }

                // Filter by amount, must be within the range
                val amountCheck =
                    if (configuration?.enableAmountFilter == true && configuration.miniSetAmount != null && configuration.maxSetAmount != null
                    ) {
                        abs(item.amount) in configuration.miniSetAmount..configuration.maxSetAmount
                    } else {
                        true
                    }

                financeTypeCheck && categoryCheck && amountCheck
            }
    }

    /**
     * Calculates the balance of the finances.
     *
     * @param finances the list of finances to be calculated
     * @return the balance of the finances
     */
    private suspend fun calculateBalance(finances: List<Finance>): Double = coroutineScope {
        val year = LocalDate.now().year

        // Group finances by finance type
        val (incomes, expenses) = finances.partition { it.category.type == FinanceType.INCOME }

        // Annual total
        val incomeTotalDeferred = async { annualCashFlow(incomes, year) }
        val expenseTotalDeferred = async { annualCashFlow(expenses, year) }

        incomeTotalDeferred.await().sum() - expenseTotalDeferred.await().sum()
    }
}
