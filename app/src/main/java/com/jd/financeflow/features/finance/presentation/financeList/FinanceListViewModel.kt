package com.jd.financeflow.features.finance.presentation.financeList

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.ViewModel

import com.jd.financeflow.features.finance.domain.FinanceRepository

/**
 * View model for the FinanceList screen.
 */
class FinanceListViewModel(
    private val financeRepository: FinanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceListState())
    val state = _state.asStateFlow()

    /**
     * Handles actions for the FinanceList screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: FinanceListActions) {
        when (action) {
            is FinanceListActions.ClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceListActions.RefreshList -> {}
            is FinanceListActions.UpdateFinanceTypeFilter -> {}
            is FinanceListActions.UpdateFinanceCategoryFilter -> {}
        }
    }
}