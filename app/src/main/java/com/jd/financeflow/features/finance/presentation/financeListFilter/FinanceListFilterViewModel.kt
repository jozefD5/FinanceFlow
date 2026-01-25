package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
                _state.update {
                    it.copy(selectedTypeIndex = action.typeIndex)
                }
            }

            is FinanceListFilterActions.OnEnableCategories -> {
                _state.update {
                    it.copy(enableCategories = action.enable)
                }
            }

            is FinanceListFilterActions.OnEnableAmountRange -> {
                _state.update {
                    it.copy(enableAmountRange = action.enable)
                }
            }

            is FinanceListFilterActions.OnUpdateAmountRange -> {}
        }
    }
}