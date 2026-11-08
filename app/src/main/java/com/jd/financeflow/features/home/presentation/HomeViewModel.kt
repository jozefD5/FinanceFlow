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
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.finance.presentation.components.annualCashFlow
import com.jd.financeflow.features.home.domain.HomeRepository


/**
 * View model for the home screen.
 *
 * @param repository repository for finance transactions
 */
class HomeViewModel(
    private val repository: HomeRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    /**
     * State flow representing the UI state of the home screen.
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