package com.jd.financeflow.features.finance.presentation.financeListFilter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FinanceListFilterViewModel() : ViewModel()  {
    private val _state = MutableStateFlow(FinanceListFilterState())
    val state = _state.asStateFlow()

    fun onAction(action: FinanceListFilterActions) {
        when (action) {
            is FinanceListFilterActions.OnClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is FinanceListFilterActions.OnUpdateType -> {
                _state.update {
                    it.copy(selectedTypeIndex = action.typeIndex)
                }
            }

            is FinanceListFilterActions.OnAddCategory -> {}

            is FinanceListFilterActions.OnRemoveCategory -> {}

            is FinanceListFilterActions.OnEnableAmountRange -> {
                _state.update {
                    it.copy(enableAmountRange = action.enable)
                }
            }

            is FinanceListFilterActions.OnUpdateAmountRange -> {}
        }
    }
}