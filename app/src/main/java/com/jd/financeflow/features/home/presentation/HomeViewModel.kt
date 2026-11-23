package com.jd.financeflow.features.home.presentation

import java.time.Clock
import java.time.LocalDate

import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.annualCashFlow
import com.jd.financeflow.features.home.domain.HomeRepository


/**
 * View model for the home screen.
 *
 * Loads all finance transactions on first subscription and derives the annual income, expense and
 * balance totals for the current year together with the list of most recent transactions.
 *
 * @param repository repository for finance transactions
 * @param clock clock used to resolve the current year, injectable for testing
 */
class HomeViewModel(
    private val repository: HomeRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())

    /**
     * State flow representing the UI state of the home screen. Triggers an initial [refresh] when
     * collection starts and stays active for 5 seconds after the last subscriber disappears.
     */
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
            is HomeActions.OnRefreshBalance -> refresh()

            is HomeActions.OnClearError -> _state.update { it.copy(error = null) }

            is HomeActions.OnAddFinanceClicked -> {}

            is HomeActions.OnAllActivityClicked -> {}
        }
    }

    /**
     * Refreshes the balance and recent transactions.
     *
     * Fetches all finances from [repository], computes the current year's annual income and expense
     * totals on [Dispatchers.Default] and keeps the 10 most recent transactions by timestamp.
     * On failure, [HomeState.error] is populated instead.
     *
     * @return the launched [kotlinx.coroutines.Job]
     */
    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val finances = when (val result = repository.getFinances()) {
            is Result.Success -> result.data
            is Result.Error -> {
                _state.update { it.copy(isLoading = false, error = result.error.toUiText()) }
                return@launch
            }
        }

        val (totalIncome, totalExpense, recent) = withContext(Dispatchers.Default) {
            val currentYear = LocalDate.now(clock).year

            // Group finances by finance type
            val (incomes, expenses) = finances.partition { it.category.type == FinanceType.INCOME }

            // Annual total by finance type
            val incomeTotalDeferred = async { annualCashFlow(incomes, currentYear) }
            val expenseTotalDeferred = async { annualCashFlow(expenses, currentYear) }

            Triple(
                incomeTotalDeferred.await().sum(),
                expenseTotalDeferred.await().sum(),
                finances
                    .sortedByDescending { items -> items.timeStampMs }
                    .take(10)
            )
        }

        // Update state
        _state.update {
            it.copy(
                isLoading = false,
                balance = totalIncome - totalExpense,
                income = totalIncome,
                expense = totalExpense,
                recentTransactions = recent
            )
        }
    }
}