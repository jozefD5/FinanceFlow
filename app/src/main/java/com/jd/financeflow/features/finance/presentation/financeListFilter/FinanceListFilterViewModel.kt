package com.jd.financeflow.features.finance.presentation.financeListFilter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.SortOrder

/**
 * View model for the finance list filter screen.
 *
 * @param configuration The initial filter configuration.
 */
class FinanceListFilterViewModel(
    private val currencyCode: String = "EUR",
    private val configuration: FilterConfiguration? = null
) : ViewModel() {
    private val _state = MutableStateFlow(
        FinanceListFilterState(
            currencyCode = currencyCode,
        )
    )
    val state = _state
        .onStart {
            if (configuration != null) {
                applyInitialConfiguration(currencyCode, configuration)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceListFilterState(currencyCode = currencyCode)
        )

    /**
     * Handles the actions performed on the screen.
     *
     * @param action The action to be performed.
     */
    fun onAction(action: FinanceListFilterActions) {
        when (action) {
            is FinanceListFilterActions.OnClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is FinanceListFilterActions.OnApplyFilter -> {
                applyFilter(action.callback)
            }

            is FinanceListFilterActions.OnResetFilter -> {
                resetFilter()
            }

            is FinanceListFilterActions.OnUpdateSort -> {
                _state.update {
                    it.copy(selectedSortIndex = action.index)
                }
            }

            is FinanceListFilterActions.OnUpdateType -> {
                updateSelectedType(action.index)
            }

            is FinanceListFilterActions.OnEnableCategories -> {
                _state.update {
                    it.copy(enableCategories = action.enable)
                }
            }

            is FinanceListFilterActions.OnCategorySelected -> {
                updateSelectedCategory(
                    financeType = action.type,
                    index = action.index
                )
            }

            is FinanceListFilterActions.OnEnableAmountRange -> {
                _state.update {
                    it.copy(enableAmountRange = action.enable)
                }
            }

            is FinanceListFilterActions.OnUpdateAmountRange -> {
                _state.update {
                    it.copy(
                        selectedAmountRangeMini = action.mini,
                        selectedAmountRangeMax = action.max
                    )
                }
            }
        }
    }


    /**
     * Updates the selected type in the state.
     *
     * @param index The index of the selected type.
     */
    private fun updateSelectedType(index: Int) {
        try {
            if (index >= 0 && index <= FinanceType.entries.size + 1) {
                val selectedType = try {
                    FinanceType.entries[index - 1]
                } catch (_: IndexOutOfBoundsException) {
                    null
                }

                _state.update {
                    it.copy(
                        selectedTypeIndex = index,
                        selectedType = selectedType
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        error = DataError.Local.INVALID_ARGUMENT.toUiText()
                    )
                }
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
        }
    }

    private fun updateSelectedCategory(
        financeType: FinanceType,
        index: Int
    ) {
        try {
            when (financeType) {
                FinanceType.INCOME -> {
                    val selectedOptions = _state.value.selectedIncomeCategories.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]

                    _state.update {
                        it.copy(selectedIncomeCategories = selectedOptions)
                    }
                }

                FinanceType.EXPENSE -> {
                    val selectedOptions =
                        _state.value.selectedExpenseCategories.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]

                    _state.update {
                        it.copy(selectedExpenseCategories = selectedOptions)
                    }
                }
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
        }
    }

    /**
     * Applies the initial configuration to the state.
     *
     * @param currencyCode The currency code to be used.
     * @param config The initial filter configuration.
     */
    private fun applyInitialConfiguration(
        currencyCode: String,
        config: FilterConfiguration
    ) {
        val currentState = state.value
        val miniAmountLimit = config.miniAmountLimit?.toFloat() ?: currentState.minimumAmount
        val maxAmountLimit = config.maxAmountLimit?.toFloat() ?: currentState.maximumAmount

        val selectedIncomeCategories =
            config.selectedIncomeIndexes ?: currentState.selectedIncomeCategories
        val selectedExpenseCategories =
            config.selectedExpenseIndexes ?: currentState.selectedExpenseCategories

        _state.update {
            it.copy(
                currencyCode = currencyCode,
                selectedSortIndex = config.sortOrder.ordinal,
                selectedTypeIndex = config.type?.ordinal ?: 0,
                selectedType = config.type,
                enableCategories = (config.selectedIncomeIndexes != null && config.selectedExpenseIndexes != null),
                selectedIncomeCategories = selectedIncomeCategories,
                selectedExpenseCategories = selectedExpenseCategories,
                enableAmountRange = (config.miniSetAmount != null && config.maxSetAmount != null),
                selectedAmountRangeMini = miniAmountLimit,
                selectedAmountRangeMax = maxAmountLimit,
            )
        }
    }

    /**
     * Applies the filter configuration to the data.
     *
     * @param callback The callback to be invoked with the filter configuration.
     */
    private fun applyFilter(callback: ((FilterConfiguration) -> Unit)?) {
        callback ?: return

        // Extract the current state
        val currentState = state.value

        // Extract the values from the current state
        val sortOrder = SortOrder.entries[currentState.selectedSortIndex]
        val type = currentState.selectedType

        val (miniAmount, maxAmount) = if (currentState.enableAmountRange) {
            Pair(
                currentState.selectedAmountRangeMini.toDouble(),
                currentState.selectedAmountRangeMax.toDouble()
            )
        } else {
            Pair(null, null)
        }

        callback.invoke(
            FilterConfiguration(
                sortOrder = sortOrder,
                type = type,
                selectedIncomeIndexes = currentState.selectedIncomeCategories,
                selectedExpenseIndexes = currentState.selectedExpenseCategories,
                miniSetAmount = miniAmount,
                maxSetAmount = maxAmount
            )
        )
    }

    /**
     * Resets the filter to its initial state.
     */
    private fun resetFilter() {
        val currentState = state.value
        _state.update {
            it.copy(
                selectedSortIndex = 0,
                selectedTypeIndex = 0,
                selectedType = null,
                enableCategories = false,
                selectedIncomeCategories = IncomeCategory.entries.map { true },
                selectedExpenseCategories = IncomeCategory.entries.map { true },
                enableAmountRange = false,
                selectedAmountRangeMini = currentState.minimumAmount,
                selectedAmountRangeMax = currentState.maximumAmount
            )
        }
    }
}