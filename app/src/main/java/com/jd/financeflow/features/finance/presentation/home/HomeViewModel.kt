package com.jd.financeflow.features.finance.presentation.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository

/**
 * View model for the home screen.
 *
 * @param repository repository for finance transactions
 */
class HomeViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart {
            refreshBalance()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

    /**
     * Handles actions for the home screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: HomeActions) {
        when (action) {
            is HomeActions.RefreshBalance -> {
                refreshBalance()
            }

            is HomeActions.ClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is HomeActions.AddTransaction -> {}

            is HomeActions.ShowBottomSheet -> {
                _state.update {
                    it.copy(showBottomSheet = action.value)
                }
            }
        }
    }

    /**
     * Refreshes the balance of the user and updates the state accordingly.
     */
    private fun refreshBalance() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        runCatching {
            when (val result = repository.getFinances()) {
                is Result.Success -> {
                    val totalIncome = repository.getTotal(FinanceType.INCOME)
                    val totalExpenses = repository.getTotal(FinanceType.EXPENSE)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            balance = totalIncome - totalExpenses,
                            income = totalIncome,
                            expense = totalExpenses
                        )
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    }
                }
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