package com.jd.financeflow.features.financeList.presentation.financeFilter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.models.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.domain.models.finance.IncomeCategory
import com.jd.financeflow.features.core.domain.models.financeFilter.FilterConfiguration
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.presentation.toUiText


/**
 * View model for the finance list filter screen.
 *
 * @param configuration The initial filter configuration.
 */
class FinanceListFilterViewModel(
    private val currencyCode: String = "EUR",
    private val configuration: FilterConfiguration? = null
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceListFilterState(currencyCode = currencyCode))
    val state = _state
        .onStart {
            configuration?.let { config ->
                applyInitialConfiguration(config)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    /**
     * Handles the actions performed on the screen.
     *
     * @param action The action to be performed.
     */
    fun onAction(action: FinanceListFilterActions) {
        when (action) {
            is FinanceListFilterActions.OnClearError -> _state.update { it.copy(error = null) }
            is FinanceListFilterActions.OnApplyFilter -> applyFilter(action.callback)
            is FinanceListFilterActions.OnResetFilter -> resetFilter()
            is FinanceListFilterActions.OnBackNavigationClicked -> {}
            is FinanceListFilterActions.OnEnableCategories -> _state.update {
                it.copy(
                    enableCategories = action.enable
                )
            }

            is FinanceListFilterActions.OnUpdateType -> updateSelectedType(action.index)
            is FinanceListFilterActions.OnCategorySelected -> updateSelectedCategory(
                financeType = action.type,
                index = action.index
            )

            is FinanceListFilterActions.OnEnableAmountRange -> _state.update {
                it.copy(
                    enableAmountRange = action.enable
                )
            }

            is FinanceListFilterActions.OnUpdateAmountRange -> _state.update {
                it.copy(
                    selectedAmountRangeMini = action.mini,
                    selectedAmountRangeMax = action.max
                )
            }
        }
    }

    /**
     * Applies the initial configuration to the state.
     *
     * @param config The initial filter configuration.
     */
    private fun applyInitialConfiguration(config: FilterConfiguration) {
        Log.i("FinanceListFilterViewModel", "Applying initial configuration: $config")

        val currentState = _state.value
        val selectedTypeIndex = config.type?.ordinal ?: 0

        val selectedIncomeCategories =
            if (config.selectedIncomeIndexes != null && config.selectedIncomeIndexes.size == IncomeCategory.entries.size) {
                config.selectedIncomeIndexes
            } else {
                currentState.selectedIncomeCategories
            }

        val selectedExpenseCategories =
            if (config.selectedExpenseIndexes != null && config.selectedExpenseIndexes.size == ExpenseCategory.entries.size) {
                config.selectedExpenseIndexes
            } else {
                currentState.selectedExpenseCategories
            }

        val miniAmountLimit = config.miniAmountLimit?.toFloat() ?: currentState.minimumAmount
        val maxAmountLimit = config.maxAmountLimit?.toFloat() ?: currentState.maximumAmount

        _state.update {
            it.copy(
                currencyCode = currencyCode,

                // Selected type and categories
                enableCategories = config.enableCategoryFilter,
                selectedTypeIndex = selectedTypeIndex,
                selectedIncomeCategories = selectedIncomeCategories,
                selectedExpenseCategories = selectedExpenseCategories,

                // Amount range
                enableAmountRange = config.enableAmountFilter,
                minimumAmount = miniAmountLimit,
                maximumAmount = maxAmountLimit,
                selectedAmountRangeMini = config.miniSetAmount?.toFloat() ?: miniAmountLimit,
                selectedAmountRangeMax = config.maxSetAmount?.toFloat() ?: maxAmountLimit
            )
        }
    }

    /**
     * Updates the selected type in the state.
     *
     * @param index The index of the selected type.
     */
    private fun updateSelectedType(index: Int) {
        val finalIndex = index.coerceIn(0, FinanceType.entries.size - 1)

        try {
            _state.update { it.copy(selectedTypeIndex = finalIndex) }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Updates the selected category (enable/disable) in the state. This changes state of category
     * (income or expense) depending on the selected type.
     *
     * @param financeType The type of finance.
     */
    private fun updateSelectedCategory(
        financeType: FinanceType,
        index: Int
    ) {
        try {
            when (financeType) {
                FinanceType.INCOME -> {
                    val selectedOptions = _state.value.selectedIncomeCategories.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]
                    _state.update { it.copy(selectedIncomeCategories = selectedOptions) }
                }

                FinanceType.EXPENSE -> {
                    val selectedOptions = _state.value.selectedExpenseCategories.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]
                    _state.update { it.copy(selectedExpenseCategories = selectedOptions) }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Applies the filter configuration to the data.
     *
     * @param callback The callback to be invoked with the filter configuration.
     */
    private fun applyFilter(callback: ((FilterConfiguration) -> Unit)?) {
        try {
            callback ?: return

            // Extract the current state
            val currentState = _state.value

            // Extract the values from the current state
            val (miniAmount, maxAmount) = if (currentState.enableAmountRange) {
                Pair(
                    currentState.selectedAmountRangeMini.toDouble(),
                    currentState.selectedAmountRangeMax.toDouble()
                )
            } else {
                Pair(null, null)
            }

            val financeType = if (currentState.enableCategories) {
                FinanceType.entries[currentState.selectedTypeIndex]
            } else {
                null
            }

            callback.invoke(
                FilterConfiguration(
                    enableCategoryFilter = currentState.enableCategories,
                    selectedIncomeIndexes = currentState.selectedIncomeCategories,
                    selectedExpenseIndexes = currentState.selectedExpenseCategories,
                    type = financeType,
                    enableAmountFilter = currentState.enableAmountRange,
                    miniSetAmount = miniAmount,
                    maxSetAmount = maxAmount
                )
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Resets the filter to its initial state.
     */
    private fun resetFilter() {
        val currentState = _state.value
        _state.update {
            it.copy(
                selectedTypeIndex = 0,
                enableCategories = false,
                selectedIncomeCategories = IncomeCategory.entries.map { true },
                selectedExpenseCategories = ExpenseCategory.entries.map { true },
                enableAmountRange = false,
                selectedAmountRangeMini = currentState.minimumAmount,
                selectedAmountRangeMax = currentState.maximumAmount
            )
        }
    }
}