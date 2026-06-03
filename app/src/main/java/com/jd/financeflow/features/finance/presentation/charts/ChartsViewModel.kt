package com.jd.financeflow.features.finance.presentation.charts

import android.util.Log
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
    fun onAction(action: FinanceChartsActions) {
        when (action) {
            is FinanceChartsActions.OnClearError -> {
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

        val incomeCashFlowDeferred =
            async(Dispatchers.Default) { calculateAveragePerMonthDay(incomes) }
        val expenseCashFlowDeferred =
            async(Dispatchers.Default) { calculateAveragePerMonthDay(expenses) }

        val incomeCashFlow = incomeCashFlowDeferred.await()
        val expenseCashFlow = expenseCashFlowDeferred.await()

        _state.update {
            it.copy(
                isLoading = false,
                incomeCashFlow = incomeCashFlow,
                expensesCashFlow = expenseCashFlow
            )
        }
    }

    private fun calculateAveragePerMonthDay1(finances: List<Finance>): List<Double> {
        val daysInMonth = 31
        val dailyAverages = MutableList(daysInMonth) { 0.0 }
        if (finances.isEmpty()) return dailyAverages

        // Calculate the number of distinct months represented in the data
        val distinctMonths = finances.map {
            val date = Instant.ofEpochMilli(it.timeStampMs).atZone(ZoneId.systemDefault())
            date.year to date.monthValue
        }.distinct().size.coerceAtLeast(1)

        finances.forEach { finance ->
            val date = Instant.ofEpochMilli(finance.timeStampMs).atZone(ZoneId.systemDefault())
            val dayOfRecord = date.dayOfMonth
            val amount = finance.amount

            when (finance.interval) {
                FinanceInterval.Daily -> {
                    for (i in 0 until daysInMonth) dailyAverages[i] += amount
                }

                FinanceInterval.Weekly -> {
                    var day = dayOfRecord
                    while (day <= daysInMonth) {
                        dailyAverages[day - 1] += amount
                        day += 7
                    }
                    day = dayOfRecord - 7
                    while (day >= 1) {
                        dailyAverages[day - 1] += amount
                        day -= 7
                    }
                }

                FinanceInterval.Monthly -> {
                    if (dayOfRecord in 1..daysInMonth) dailyAverages[dayOfRecord - 1] += amount
                }

                FinanceInterval.Quarterly -> {
                    if (dayOfRecord in 1..daysInMonth) dailyAverages[dayOfRecord - 1] += amount / 3.0
                }

                FinanceInterval.HalfYearly -> {
                    if (dayOfRecord in 1..daysInMonth) dailyAverages[dayOfRecord - 1] += amount / 6.0
                }

                FinanceInterval.Yearly -> {
                    if (dayOfRecord in 1..daysInMonth) dailyAverages[dayOfRecord - 1] += amount / 12.0
                }
            }
        }

        // Divide by the number of months to get the average typical month
        return dailyAverages.map { it / distinctMonths }
    }

    private fun calculateAveragePerMonthDay(finances: List<Finance>): List<Double> {
        val daysInMonth = 31
        val weeksInMonth = 4
        val dailyAverages = MutableList(daysInMonth) { 0.0 }

        // Group items by day of month
        val groupedByInterval = finances.groupBy { item -> item.interval }

        groupedByInterval.forEach { (interval, finances) ->
            when (interval) {
                FinanceInterval.Daily -> {
                    for (i in 0 until daysInMonth) {
                        dailyAverages[i] += finances.sumOf { it.amount }
                    }
                }

                FinanceInterval.Weekly -> {
                    val groupedByDay = finances.groupBy { item ->
                        Instant.ofEpochMilli(item.timeStampMs)
                            .atZone(ZoneId.systemDefault())
                            .dayOfMonth
                    }

                    groupedByDay.forEach { (dayIndex, subFinance) ->
                        for (i in 0 until weeksInMonth) {
                            val actualIndex: Int = dayIndex + (i * 7)
                            if (actualIndex <= daysInMonth) {
                                dailyAverages[actualIndex] = subFinance.sumOf { it.amount }
                            }
                        }
                    }
                }

                FinanceInterval.Monthly -> {
                    val groupedByDay = finances.groupBy { item ->
                        Instant.ofEpochMilli(item.timeStampMs)
                            .atZone(ZoneId.systemDefault())
                            .dayOfMonth
                    }

                    groupedByDay.forEach { (dayIndex, subFinance) ->
                        dailyAverages[dayIndex] += subFinance.sumOf { it.amount }
                    }
                }

                else -> {}
            }
        }

        return dailyAverages
    }

}
