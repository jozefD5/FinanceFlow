package com.jd.financeflow.features.finance.presentation.charts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.onError
import com.jd.financeflow.features.core.domain.onSuccess
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData
import com.jd.financeflow.features.finance.presentation.components.toUiText


class ChartsViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceChartsState(currencyCode = currencyCode))
    val state = _state
        .onStart {
            refreshBarData()
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
     * Refresh income and expense chart data
     */
    private fun refreshBarData() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            refreshIncomeBarData()
            refreshExpensesBarData()
            _state.update { it.copy(isLoading = false) }
        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
        }
    }

    /**
     * Refresh/Update income state fields representing bar data
     */
    private suspend fun refreshIncomeBarData() {
        val barData = mutableListOf<BarChartData>()

        IncomeCategory.entries.forEach { category ->
            val incomeCategory = FinanceCategory.Income(category)
            repository.getTotal(type = FinanceType.INCOME, category = incomeCategory)
                .onSuccess { values ->
                    barData.add(
                        BarChartData(
                            label = incomeCategory.toUiText(),
                            value = values.toFloat()
                        )
                    )
                }
                .onError { error ->
                    _state.update { it.copy(error = error.toUiText()) }
                    return@onError
                }
        }

        if (barData.isEmpty()) {
            _state.update {
                it.copy(
                    maxIncome = 0f,
                    averageIncome = 0f,
                    incomeCategoryChartData = emptyList()
                )
            }
        } else {
            _state.update { it ->
                it.copy(
                    maxIncome = barData.maxBy { item -> item.value }.value,
                    averageIncome = barData.map { it.value }.average().toFloat(),
                    incomeCategoryChartData = barData
                )
            }
        }
    }

    /**
     * Refresh/Update expenses state fields representing bar data
     */
    private suspend fun refreshExpensesBarData() {
        val barData = mutableListOf<BarChartData>()

        ExpenseCategory.entries.forEach { category ->
            val expenseCategory = FinanceCategory.Expense(category)
            repository.getTotal(type = FinanceType.EXPENSE, category = expenseCategory)
                .onSuccess { values ->
                    barData.add(
                        BarChartData(
                            label = expenseCategory.toUiText(),
                            value = values.toFloat()
                        )
                    )
                }
                .onError { error ->
                    _state.update { it.copy(error = error.toUiText()) }
                    return@onError
                }
        }

        if (barData.isEmpty()) {
            _state.update {
                it.copy(
                    maxExpenses = 0f,
                    averageExpenses = 0f,
                    expensesCategoryChartData = emptyList()
                )
            }
        } else
            _state.update { it ->
                it.copy(
                    maxExpenses = barData.maxBy { item -> item.value }.value,
                    averageExpenses = barData.map { it.value }.average().toFloat(),
                    expensesCategoryChartData = barData
                )
            }
    }
}
