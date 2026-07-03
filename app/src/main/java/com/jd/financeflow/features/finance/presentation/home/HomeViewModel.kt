package com.jd.financeflow.features.finance.presentation.home

import java.time.LocalDate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.presentation.components.annualCashFlow


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
            refresh()
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
            is HomeActions.OnRefreshBalance -> {
                refresh()
            }

            is HomeActions.OnClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is HomeActions.OnAddFinanceClicked -> {}
        }
    }

    /**
     * Refreshes the balance and recent transactions.
     */
    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val year = LocalDate.now().year
        val finances = when (val result = repository.getFinances()) {
            is Result.Success -> result.data
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

        // Group finances by finance type
        val (incomes, expenses) = finances.partition { it.category.type == FinanceType.INCOME }

        // Annual total by category
        val incomeCategoryTotalDeferred =
            async(Dispatchers.Default) { annualCashFlow(incomes, year) }
        val expenseCategoryTotalDeferred =
            async(Dispatchers.Default) { annualCashFlow(expenses, year) }

        // Categories
        val totalIncome = incomeCategoryTotalDeferred.await().sum()
        val totalExpense = expenseCategoryTotalDeferred.await().sum()

        // Update state
        _state.update {
            it.copy(
                isLoading = false,
                balance = totalIncome - totalExpense,
                income = totalIncome,
                expense = totalExpense,
                recentTransactions = finances
                    .sortedByDescending { items -> items.timeStampMs }
                    .take(5)
            )
        }
    }
}