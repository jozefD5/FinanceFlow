package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import android.util.Log
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
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.presentation.components.endTimeDuration
import com.jd.financeflow.features.finance.presentation.components.monthlyCashFlow

/**
 * Finance chart card view model
 *
 * @param repository Finance repository
 * @param startTimeMs Start time in milliseconds
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 */
class FinanceChartCardViewModel(
    private val repository: FinanceRepository,
    private val startTimeMs: Long,
    // private val interval - TODO, this will allow to switch between weekly, 6month and annual
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceChartCardState(currencyCode = currencyCode))

    val state = _state
        .onStart {
            refreshChart()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    /**
     * On action handler
     *
     * @param action Action to be handled
     */
    fun onAction(action: FinanceChartCardActions) {
        when (action) {
            is FinanceChartCardActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceChartCardActions.OnRefresh -> {
                refreshChart()
            }

            is FinanceChartCardActions.OnEnableIncome -> {
                _state.update { it.copy(incomeLineEnable = action.enable) }
            }

            is FinanceChartCardActions.OnEnableExpense -> {
                _state.update { it.copy(expenseLineEnable = action.enable) }
            }

            is FinanceChartCardActions.OnEnableBalance -> {
                _state.update { it.copy(balanceLineEnable = action.enable) }
            }
        }
    }

    /**
     * Refresh chart data with latest finance data
     */
    private fun refreshChart() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        // Get latest finance list from repository and split it into income and expense
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

        val (incomes, expenses) = finances.partition { it.category.type == FinanceType.INCOME }

        // Calculate monthly cash flow for income and expense
        val incomeDiffered = async(Dispatchers.Default) {
            monthlyCashFlow(
                items = incomes,
                startTimeMs = startTimeMs,
                endTimeMs = startTimeMs.endTimeDuration(FinanceInterval.Monthly)
            )
        }
        val expenseDiffered = async(Dispatchers.Default) {
            monthlyCashFlow(
                items = expenses,
                startTimeMs = startTimeMs,
                endTimeMs = startTimeMs.endTimeDuration(FinanceInterval.Monthly)
            )
        }

        val income = incomeDiffered.await()
        val expense = expenseDiffered.await()

        // Calculate balance
        var runningBalance = 0.0
        val balance = income.mapIndexed { index, income ->
            runningBalance += income - (expense.getOrNull(index) ?: 0.0)
            runningBalance
        }

        Log.d("FinanceChartCardViewModel", "income: $income")
        Log.d("FinanceChartCardViewModel", "expense: $expense")
        Log.d("FinanceChartCardViewModel", "balance: $balance")


        _state.update {
            it.copy(
                isLoading = false,
                income = income,
                expense = expense,
                balance = balance
            )
        }
    }
}