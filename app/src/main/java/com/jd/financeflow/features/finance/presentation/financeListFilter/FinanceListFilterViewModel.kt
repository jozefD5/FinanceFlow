package com.jd.financeflow.features.finance.presentation.financeListFilter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.ViewModel

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.SortOrder


/**
 * View model for the finance list filter screen.
 */
class FinanceListFilterViewModel() : ViewModel() {
    private val _state = MutableStateFlow(FinanceListFilterState())
    val state = _state.asStateFlow()

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
                    val selectedOptions = _state.value.categoryIncomeSelectedOptions.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]

                    _state.update {
                        it.copy(categoryIncomeSelectedOptions = selectedOptions)
                    }
                }

                FinanceType.EXPENSE -> {
                    val selectedOptions =
                        _state.value.categoryExpenseSelectedOptions.toMutableList()
                    selectedOptions[index] = !selectedOptions[index]

                    _state.update {
                        it.copy(categoryExpenseSelectedOptions = selectedOptions)
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

        // Streamlined category selection
        val filterCategories = when (type) {
            FinanceType.INCOME -> currentState.categoryIncomeOptions
                .filterIndexed { index, _ -> currentState.categoryIncomeSelectedOptions[index] }

            FinanceType.EXPENSE -> currentState.categoryExpenseOptions
                .filterIndexed { index, _ -> currentState.categoryExpenseSelectedOptions[index] }

            else -> emptyList()
        }.ifEmpty { null }

        callback.invoke(
            FilterConfiguration(
                sortOrder = sortOrder,
                type = type,
                categories = filterCategories,
                miniAmount = miniAmount,
                maxAmount = maxAmount
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
                categoryIncomeSelectedOptions = IncomeCategory.entries.map { true },
                categoryExpenseSelectedOptions = IncomeCategory.entries.map { true },
                enableAmountRange = false,
                selectedAmountRangeMini = currentState.minimumAmount,
                selectedAmountRangeMax = currentState.maximumAmount
            )
        }
    }
}