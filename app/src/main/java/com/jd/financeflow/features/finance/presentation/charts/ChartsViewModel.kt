package com.jd.financeflow.features.finance.presentation.charts

import java.time.Instant
import java.time.ZoneId

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.FinanceType

// Helper extension for readability
private val Finance.zonedDateTime
    get() = Instant.ofEpochMilli(timeStampMs).atZone(ZoneId.systemDefault())

class ChartsViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceChartsState(currencyCode = currencyCode))
    val state = _state
        .onStart {
            refreshCashFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceChartsState(currencyCode = currencyCode)
        )


    /**
     * On action handler
     * @param action Action to be handled
     */
    fun onAction(action: ChartsActions) {
        when (action) {
            is ChartsActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    /**
     * Calculate average monthly cash flow using incomes and expenses
     */
    private fun refreshCashFlow() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

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

        // Cash flow
        val incomeCashFlowDeferred =
            async(Dispatchers.Default) { calculateAveragePerMonthDay(incomes) }
        val expenseCashFlowDeferred =
            async(Dispatchers.Default) { calculateAveragePerMonthDay(expenses) }

        // Monthly average
        val monthlyIncomeDiffered =
            async(Dispatchers.Default) { calculateTotalPerMonth(incomes) }
        val monthlyExpenseDiffered =
            async(Dispatchers.Default) { calculateTotalPerMonth(expenses) }

        // Final values
        val incomeCashFlow = incomeCashFlowDeferred.await()
        val expenseCashFlow = expenseCashFlowDeferred.await()
        val monthlyIncome = monthlyIncomeDiffered.await()
        val monthlyExpense = monthlyExpenseDiffered.await()

        _state.update {
            it.copy(
                isLoading = false,
                incomeCashFlow = incomeCashFlow,
                expenseCashFlow = expenseCashFlow,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense
            )
        }
    }

    /**
     * Calculate average monthly values for each day of the month
     *
     * @param financeItems finances used for calculation
     */
    private fun calculateAveragePerMonthDay(financeItems: List<Finance>): List<Double> {
        val daysInMonth = 31
        val dailyAverages = MutableList(daysInMonth) { 0.0 }

        financeItems.forEach { item ->
            val dayIndex = item.zonedDateTime.dayOfMonth - 1

            when (item.interval) {
                FinanceInterval.Daily -> {
                    for (i in 0 until daysInMonth) dailyAverages[i] += item.amount
                }

                FinanceInterval.Weekly -> {
                    // Apply to the specific day and every 7 days forward/backward in the month
                    for (i in -4..4) {
                        val actualIndex = dayIndex + (i * 7)
                        if (actualIndex in 0 until daysInMonth) {
                            dailyAverages[actualIndex] += item.amount
                        }
                    }
                }

                FinanceInterval.Monthly -> {
                    if (dayIndex in 0 until daysInMonth) {
                        dailyAverages[dayIndex] += item.amount
                    }
                }

                else -> 0.0
            }
        }

        return dailyAverages
    }

    /**
     * Calculate total amount for each month.
     *
     * @param financeItems finance items used for processing
     */
    private fun calculateTotalPerMonth(financeItems: List<Finance>): List<Double> {
        val monthsInYear = 12
        val monthlyTotal = MutableList(monthsInYear) { 0.0 }

        financeItems.forEach { item ->
            val date = Instant.ofEpochMilli(item.timeStampMs).atZone(ZoneId.systemDefault())
            val startMonth = date.monthValue - 1

            when (item.interval) {
                // These apply to EVERY month starting from the first record
                FinanceInterval.Daily -> {
                    val monthlyAmount = item.amount * 30.44
                    for (i in startMonth until monthsInYear) monthlyTotal[i] += monthlyAmount
                }

                FinanceInterval.Weekly -> {
                    val monthlyAmount = item.amount * 4.34
                    for (i in startMonth until monthsInYear) monthlyTotal[i] += monthlyAmount
                }

                FinanceInterval.Monthly -> {
                    for (i in startMonth until monthsInYear) monthlyTotal[i] += item.amount
                }

                // Multi-month spikes
                FinanceInterval.Quarterly -> {
                    for (i in 0 until monthsInYear step 3) {
                        val idx = startMonth + i
                        if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                    }
                }

                FinanceInterval.HalfYearly -> {
                    for (i in 0 until monthsInYear step 6) {
                        val idx = startMonth + i
                        if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                    }
                }

                FinanceInterval.Yearly -> {
                    monthlyTotal[startMonth] += item.amount
                }
            }
        }
        return monthlyTotal
    }

}
