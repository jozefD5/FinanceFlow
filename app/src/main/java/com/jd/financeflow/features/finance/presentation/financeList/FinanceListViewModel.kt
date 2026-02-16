package com.jd.financeflow.features.finance.presentation.financeList

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlinx.coroutines.Job
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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
import com.jd.financeflow.features.finance.domain.models.SortOrder


/**
 * View model for the FinanceList screen.
 */
class FinanceListViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
    private val filterConfiguration: FilterConfiguration? = null
) : ViewModel() {
    private var searchJob: Job? = null
    private var cachedFinances: List<Finance> = emptyList()

    private val _state = MutableStateFlow(
        FinanceListState(
            currencyCode = currencyCode,
            filterConfiguration = filterConfiguration
        )
    )
    val state = _state
        .onStart {
            applyFilterConfiguration(filterConfiguration)
            observeSearchQuery()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceListState()
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
            is FinanceListActions.OnFinanceItemDelete -> {
                deleteFinance(action.finance)
            }

            is FinanceListActions.OnSelectedDeleteFinanceItem -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = action.shoeDialog,
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

            is FinanceListActions.OnSearch -> {
                refreshFinanceList(query = state.value.searchQuery)
            }

            is FinanceListActions.OnRefresh -> {
                val currentState = state.value

                // Refresh with current search query if any is available else refresh with current
                // filter configuration
                if (currentState.searchQuery.isNotBlank()) {
                    refreshFinanceList(
                        refresh = true,
                        query = currentState.searchQuery,
                    )
                } else {
                    refreshFinanceList(
                        refresh = true,
                        filterConfiguration = currentState.filterConfiguration
                    )
                }
            }

            is FinanceListActions.OnUpdateFilterConfiguration -> {}

        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update { it.copy(finances = cachedFinances) }
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
     * Apply filter configuration.
     *
     * @param config new filter configuration
     */
    private fun applyFilterConfiguration(config: FilterConfiguration?) = viewModelScope.launch {
        // Get maximum value of finances (+10%) or set to default value
        val highestAmount: Double = runCatching {
            when (val result = repository.getFinances()) {
                is Result.Success -> {
                    val value = if (result.data.isEmpty()) {
                        null
                    } else {
                        result.data.maxOfOrNull { it.amount }
                    }

                    // Round up to nearest 1000
                    val rawValue = value?.times(1.1) ?: 10_000.0
                    ceil(rawValue / 1000.0) * 1000.0
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    }
                    return@launch
                }
            }
        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
            return@launch
        }

        // If provided, use supplied configuration with updated max amount limit. Else, Create new
        // filtered configuration object.
        val updatedConfig = when (config) {
            is FilterConfiguration -> {
                config.copy(
                    miniAmountLimit = 0.0,
                    maxAmountLimit = highestAmount
                )
            }

            null -> {
                FilterConfiguration(
                    sortOrder = SortOrder.NEWEST_FIRST,
                    miniAmountLimit = 0.0,
                    maxAmountLimit = highestAmount
                )
            }
        }

        // Update state with new configuration
        _state.update {
            it.copy(
                currencyCode = currencyCode,
                filterConfiguration = updatedConfig,
            )
        }

        refreshFinanceList(refresh = true, filterConfiguration = updatedConfig)
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
    private fun refreshFinanceList(
        refresh: Boolean = false,
        query: String? = null,
        filterConfiguration: FilterConfiguration? = null
    ) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            val items = if (refresh) {
                Log.i("FinanceListViewModel", "Refreshing finance list: refresh = true")
                when (val result = repository.getFinances()) {
                    is Result.Success -> {
                        cachedFinances = result.data
                        result.data
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.error.toUiText()
                            )
                        }
                        return@launch
                    }
                }
            } else {
                Log.i("FinanceListViewModel", "Refreshing finance list: refresh = false")
                cachedFinances
            }

            // Filter items by query or filter configuration. If none are provided, use all items.
            val filteredItems = if (!query.isNullOrBlank()) {
                Log.i("FinanceListViewModel", "Filtering finance list: query = $query")
                filterItemsByQuery(items, query)
            } else if (filterConfiguration != null) {
                Log.i(
                    "FinanceListViewModel",
                    "Filtering finance list: filterConfiguration = $filterConfiguration"
                )
                filterItemsByFilter(items, filterConfiguration)
            } else {
                Log.i("FinanceListViewModel", "Filtering finance list: no filter")
                items
            }

            Log.i("FinanceListViewModel", "Filtered finance list: ${filteredItems.size}")

            _state.update {
                it.copy(
                    isLoading = false,
                    finances = filteredItems
                )
            }

        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText()
                )
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
    private fun filterItemsByQuery(items: List<Finance>, query: String?): List<Finance> {
        if (query.isNullOrBlank()) {
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
        filterConfiguration: FilterConfiguration?
    ): List<Finance> {
        if (filterConfiguration == null) {
            return items
        }

        val filteredItems = items
            .filter { finance ->
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
                        if (categoryCheckList.isEmpty()) {
                            true
                        } else {
                            categoryCheckList.contains(finance.category)
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

        // Sort items by sort order
        return when (filterConfiguration.sortOrder) {
            SortOrder.AMOUNT_ASCENDING -> filteredItems.sortedWith(compareBy({ it.amount }, { it.id }))
            SortOrder.AMOUNT_DESCENDING -> filteredItems.sortedWith(compareByDescending<Finance> { it.amount }.thenBy { it.id })
            SortOrder.NEWEST_FIRST -> filteredItems.sortedWith(compareByDescending<Finance> { it.createdAt }.thenBy { it.id })
            SortOrder.OLDEST_FIRST -> filteredItems.sortedWith(compareBy<Finance> { it.createdAt }.thenBy { it.id })
        }
    }

    /**
     * Deletes a finance.
     *
     * @param finance finance to be deleted
     */
    private fun deleteFinance(finance: Finance) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val currentState = state.value

        runCatching {
            when (val result = repository.deleteFinance(finance)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            selectedDeleteFinance = null,
                            showDeleteDialog = false
                        )
                    }

                    refreshFinanceList(
                        refresh = true,
                        filterConfiguration = currentState.filterConfiguration
                    )
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText(),
                            selectedDeleteFinance = null,
                            showDeleteDialog = false
                        )
                    }
                }
            }
        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText(),
                    selectedDeleteFinance = null,
                    showDeleteDialog = false
                )
            }
        }
    }
}
