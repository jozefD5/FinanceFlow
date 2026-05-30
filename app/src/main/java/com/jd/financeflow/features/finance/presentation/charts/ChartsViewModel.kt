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

    private fun calculateAveragePerMonthDay(finances: List<Finance>): List<Double> {
        val daysInMonth = 31
        val dailyAverages = MutableList(daysInMonth) { 0.0 }

        // Group items by day of month
        val groupedFinances = finances.groupBy { item ->
            Instant.ofEpochMilli(item.timeStampMs)
                .atZone(ZoneId.systemDefault())
                .dayOfMonth
        }

        // Calculate average financial amount for each day of the month.
        // We normalize recurring intervals to a daily average based on a 365-day year.
        groupedFinances.forEach { (dayOfMonth, items) ->
            if (dayOfMonth in 1..daysInMonth) {
                val dayTotal = items.groupBy { it.interval }.entries.sumOf { (interval, subItems) ->
                    val sum = subItems.sumOf { it.amount }
                    when (interval) {
                        FinanceInterval.Daily -> sum
                        FinanceInterval.Weekly -> (sum * 52) / 365.0
                        FinanceInterval.Monthly -> (sum * 12) / 365.0
                        FinanceInterval.Quarterly -> (sum * 4) / 365.0
                        FinanceInterval.HalfYearly -> (sum * 2) / 365.0
                        FinanceInterval.Yearly, FinanceInterval.Once -> sum / 365.0
                    }
                }
                dailyAverages[dayOfMonth - 1] = dayTotal
            }
        }

        return dailyAverages
    }


}
