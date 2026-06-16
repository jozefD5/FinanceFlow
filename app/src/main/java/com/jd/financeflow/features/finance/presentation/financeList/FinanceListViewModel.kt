package com.jd.financeflow.features.finance.presentation.financeList

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.calculateAnnualAmount
import kotlinx.coroutines.flow.StateFlow


private const val TAG = "FinanceListViewModel"

class FinanceListViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String,
    private val filterConfiguration: FilterConfiguration? = null
) : ViewModel() {

    private var cachedFinances: List<Finance> = emptyList()

    private val _state = MutableStateFlow(FinanceListState(currencyCode = currencyCode))
    val state: StateFlow<FinanceListState> = _state
        .onStart {
            applyFilterConfiguration(filterConfiguration)
            observeSearchQuery()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FinanceListState()
        )

    /**
     * Handles actions for the FinanceList screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: FinanceListActions) {
        when (action) {
            is FinanceListActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceListActions.OnAddFinanceClicked -> {}
            is FinanceListActions.OnFilterClicked -> {}
            is FinanceListActions.OnFinanceItemClicked -> {}

            is FinanceListActions.OnSelectedDeleteFinanceItem -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = action.showDialog,
                        selectedDeleteFinance = action.finance
                    )
                }
            }

            is FinanceListActions.OnEnableSearch -> {
                _state.update { it.copy(isSearching = action.value) }
            }

            is FinanceListActions.OnUpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = action.value) }
            }

            is FinanceListActions.OnSearch -> {}

            is FinanceListActions.OnRefresh -> {
                manualRefresh()
            }

            is FinanceListActions.OnFinanceItemDelete -> {
                deleteFinance(action.finance)
            }
        }
    }

    /**
     * Applies filter configuration.
     *
     * @param config filter configuration to be applied
     */
    private fun applyFilterConfiguration(config: FilterConfiguration?) = viewModelScope.launch {
        if (config == null) {
            refreshFinanceList(refresh = true)
            return@launch
        }

        // Apply price range filter logic here if needed, eg. rounded values.
        val updatedConfig = config.copy(
            miniSetAmount = config.miniSetAmount?.let { amount ->
                val rawValue = amount.times(1.1)
                ceil(rawValue / 1000.0) * 1000.0
            },
            maxSetAmount = config.maxSetAmount?.let { amount ->
                val rawValue = amount.times(1.1)
                ceil(rawValue / 1000.0) * 1000.0
            }
        )

        _state.update { it.copy(isLoading = true) }

        val filteredItems = filterItemsByFilter(cachedFinances, updatedConfig)

        _state.update {
            it.copy(
                isLoading = false,
                finances = filteredItems,
                filterConfiguration = updatedConfig
            )
        }
    }

    /**
     * Observes the search query and filters the list accordingly.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        _state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L.milliseconds)
            .flatMapLatest { query ->
                flow {
                    when {
                        query.isBlank() -> {
                            emit(Unit)
                        }

                        query.length >= 2 -> {
                            emit(Unit)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Manually refreshes the finance list.
     */
    private fun manualRefresh() = viewModelScope.launch {
        refreshFinanceList(refresh = true)
    }

    /**
     * Deletes the provided finance item.
     *
     * @param finance finance item to be deleted
     */
    private fun deleteFinance(finance: Finance) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = repository.deleteFinance(finance)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        showDeleteDialog = false,
                        selectedDeleteFinance = null
                    )
                }
                refreshFinanceList(refresh = true)
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
     * Refreshes the list of finances.
     *
     * @param refresh whether to get new list form local storage or use current list. Only one
     * search field can be used, [query] or [filterConfiguration]. Prior option has precedence.
     *
     * @param query the query to be used for filtering
     * @param filterConfiguration the filter configuration to be used for filtering
     */
    private suspend fun refreshFinanceList(
        refresh: Boolean = false,
        query: String? = null,
        filterConfiguration: FilterConfiguration? = null
    ) {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            // Get finances from local storage if refresh is true.
            if (refresh) {
                when (val result = repository.getFinances()) {
                    is Result.Success -> {
                        cachedFinances = result.data.sortedByDescending { it.timeStampMs }
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = result.error.toUiText())
                        }
                        return@runCatching
                    }
                }
            }

            // Filter items by query or filter configuration. If none are provided, use all items.
            val filteredItems = if (!query.isNullOrBlank()) {
                filterItemsByQuery(cachedFinances, query)
            } else if (filterConfiguration != null) {
                filterItemsByFilter(cachedFinances, filterConfiguration)
            } else {
                Log.i("FinanceListViewModel", "Filtering finance list: no filter")
                cachedFinances
            }

            val currentBalance = calculateBalance(filteredItems)

            _state.update {
                it.copy(
                    isLoading = false,
                    finances = filteredItems,
                    currentBalance = currentBalance
                )
            }

        }.getOrElse { error ->
            Log.e(TAG, "Error refreshing finance list", error)
            _state.update {
                it.copy(isLoading = false, error = DataError.Local.UNKNOWN.toUiText())
            }
        }
    }

    /**
     * Filters the items by the provided query.
     *
     * @param items the list of items to be filtered
     * @param query the query to be used for filtering
     * @return the filtered list of items
     */
    private fun filterItemsByQuery(items: List<Finance>, query: String): List<Finance> {
        if (query.isBlank()) {
            return items
        }

        return items.filter { finance ->
            finance.title.contains(query, ignoreCase = true) ||
                    finance.description?.contains(query, ignoreCase = true) == true
        }
    }

    /**
     * Filters the items by the provided filter configuration.
     *
     * @param items the list of items to be filtered
     * @param filterConfiguration the filter configuration to be used for filtering
     * @return the filtered list of items
     */
    private fun filterItemsByFilter(
        items: List<Finance>,
        filterConfiguration: FilterConfiguration
    ): List<Finance> {
        return items.filter { finance ->
            // Filter by finance type
            val financeTypeCheck = when (filterConfiguration.type) {
                FinanceType.INCOME -> finance.category is FinanceCategory.Income
                FinanceType.EXPENSE -> finance.category is FinanceCategory.Expense
                null -> true
            }

            // Filter by category
            val categoryCheck =
                if (filterConfiguration.enableCategoryFilter && filterConfiguration.type != null) {
                    val categoryCheckList = mutableListOf<FinanceCategory>()

                    // Add all selected categories to comparison/check list
                    when (filterConfiguration.type) {
                        FinanceType.INCOME -> {
                            IncomeCategory.entries.forEachIndexed { index, category ->
                                if (filterConfiguration.selectedIncomeIndexes?.get(index) == true) {
                                    categoryCheckList.add(FinanceCategory.Income(category))
                                }
                            }
                        }

                        FinanceType.EXPENSE -> {
                            ExpenseCategory.entries.forEachIndexed { index, category ->
                                if (filterConfiguration.selectedExpenseIndexes?.get(index) == true) {
                                    categoryCheckList.add(FinanceCategory.Expense(category))
                                }
                            }
                        }
                    }

                    // Check if finance category is in comparison/check list
                    if (categoryCheckList.isNotEmpty()) {
                        categoryCheckList.contains(finance.category)
                    } else {
                        true
                    }
                } else {
                    true
                }

            // Filter by amount, must be within the range
            val amountCheck = if (filterConfiguration.enableAmountFilter &&
                filterConfiguration.miniSetAmount != null &&
                filterConfiguration.maxSetAmount != null
            ) {
                finance.amount in filterConfiguration.miniSetAmount..filterConfiguration.maxSetAmount
            } else {
                true
            }

            financeTypeCheck && categoryCheck && amountCheck
        }
    }

    private suspend fun calculateBalance(finances: List<Finance>): Double =
        withContext(Dispatchers.Default) {
            finances.sumOf { item ->
                val annualAmount = calculateAnnualAmount(item)
                if (item.category.type == FinanceType.INCOME) annualAmount else -annualAmount
            }
        }
}
