package com.jd.financeflow.features.finance.presentation.financeListFilter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.ViewModel

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.models.FinanceType


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

            is FinanceListFilterActions.OnApplyFilter -> {}

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

            is FinanceListFilterActions.OnUpdateAmountRange -> {}
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
}