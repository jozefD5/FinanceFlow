package com.jd.financeflow.features.finance.presentation.financeList

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository


/**
 * View model for the FinanceList screen.
 */
class FinanceListViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceListState())
    val state = _state
        .onStart {
            updateFinances()
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
            is FinanceListActions.ClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceListActions.FinanceClicked -> {}
            is FinanceListActions.UpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = action.value) }
            }

            is FinanceListActions.OnSearch -> {
                updateFinances()
            }
            is FinanceListActions.UpdateFinanceTypeFilter -> {}
            is FinanceListActions.UpdateFinanceCategoryFilter -> {}
        }
    }

    /**
     * Refreshes the list of finances.
     */
    fun updateFinances() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            val items = when (val result = repository.getFinances()) {
                is Result.Success -> {
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

            _state.update {
                it.copy(
                    isLoading = false,
                    finances = items
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
}