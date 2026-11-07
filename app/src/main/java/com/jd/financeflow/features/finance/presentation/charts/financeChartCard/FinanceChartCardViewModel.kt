package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import java.time.Instant
import java.time.ZoneId

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.collections.component1
import kotlin.collections.component2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.annualCashFlow
import com.jd.financeflow.features.finance.presentation.components.annualTotalByCategory
import com.jd.financeflow.features.finance.presentation.components.monthlyCashFlow
import com.jd.financeflow.features.finance.presentation.components.toUiText
import com.jd.financeflow.features.finance.presentation.components.monthlyTotalByCategoryRange


/**
 * Finance chart card view model
 *
 * @param repository Finance repository
 * @param startTimeMs Start time in milliseconds
 * @param interval Interval of the chart
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 */
class FinanceChartCardViewModel(
    private val repository: FinanceRepository,
    private val startTimeMs: Long,
    private val interval: FinanceInterval,
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

    val chartInterval = interval

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
        }
    }

    /**
     * Refresh chart data with latest finance data
     */
    private fun refreshChart() {
        when (interval) {
            FinanceInterval.Monthly -> calculateMonthlyInterval()
            FinanceInterval.Annually -> calculateAnnualInterval()
            else -> {}
        }
    }

    /**
     * Calculate monthly interval data
     */
    private fun calculateMonthlyInterval() = viewModelScope.launch {
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

        // Convert start time to year and month
        val zonedDateTime = Instant.ofEpochMilli(startTimeMs).atZone(ZoneId.systemDefault())
        val year = zonedDateTime.year
        val month = zonedDateTime.monthValue

        // Calculate key values asynchronously
        val incomeDiffered = async(Dispatchers.Default) {
            monthlyCashFlow(
                items = incomes,
                year = year,
                month = month
            )
        }
        val expenseDiffered = async(Dispatchers.Default) {
            monthlyCashFlow(
                items = expenses,
                year = year,
                month = month
            )
        }
        val incomeCategoryDiffered = async(Dispatchers.Default) {
            monthlyTotalByCategoryRange(
                items = incomes,
                year = year,
                month = month
            )
        }
        val expenseCategoryDiffered = async(Dispatchers.Default) {
            monthlyTotalByCategoryRange(
                items = expenses,
                year = year,
                month = month
            )
        }

        // Get final values and calculate total
        val income = incomeDiffered.await()
        val expense = expenseDiffered.await()
        val finalBalance = income.sum() - expense.sum()

        var balanceRecord = 0.0
        val balanceLevel = DoubleArray(income.size)
        income.forEachIndexed { index, transaction ->
            val result = (transaction + balanceRecord) - (expense.getOrNull(index) ?: 0.0)
            balanceLevel[index] = result
            balanceRecord = result
        }

        val incomeCategories = incomeCategoryDiffered
            .await()
            .map { (order, amount) ->
                val category = when (order) {
                    IncomeCategory.Salary.ordinal -> IncomeCategory.Salary
                    IncomeCategory.Bonus.ordinal -> IncomeCategory.Bonus
                    IncomeCategory.Commission.ordinal -> IncomeCategory.Commission
                    IncomeCategory.Benefit.ordinal -> IncomeCategory.Benefit
                    IncomeCategory.Investment.ordinal -> IncomeCategory.Investment
                    else -> IncomeCategory.Other
                }
                Pair(
                    FinanceCategory.Income(category).toUiText(),
                    amount
                )
            }
            .sortedByDescending { it.second }

        val expenseCategories = expenseCategoryDiffered
            .await()
            .map { (order, amount) ->
                val category = when (order) {
                    ExpenseCategory.Housing.ordinal -> ExpenseCategory.Housing
                    ExpenseCategory.Utility.ordinal -> ExpenseCategory.Utility
                    ExpenseCategory.Food.ordinal -> ExpenseCategory.Food
                    ExpenseCategory.Transport.ordinal -> ExpenseCategory.Transport
                    ExpenseCategory.Health.ordinal -> ExpenseCategory.Health
                    ExpenseCategory.Education.ordinal -> ExpenseCategory.Education
                    ExpenseCategory.Entertainment.ordinal -> ExpenseCategory.Entertainment
                    else -> ExpenseCategory.Other
                }
                Pair(
                    FinanceCategory.Expense(category).toUiText(),
                    amount
                )
            }
            .sortedByDescending { it.second }

        _state.update {
            it.copy(
                isLoading = false,
                dataAvailable = income.sum() > 0 || expense.sum() > 0,
                balance = finalBalance,
                incomeTransactions = income,
                expenseTransactions = expense,
                balanceLevel = balanceLevel.toList(),
                incomeCategories = incomeCategories,
                expenseCategories = expenseCategories
            )
        }
    }

    /**
     * Calculate annual interval data
     */
    private fun calculateAnnualInterval() = viewModelScope.launch {
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

        // Convert start time to year and month
        val zonedDateTime = Instant.ofEpochMilli(startTimeMs).atZone(ZoneId.systemDefault())
        val year = zonedDateTime.year

        // Calculate key values asynchronously
        val incomeDiffered = async(Dispatchers.Default) {
            annualCashFlow(incomes, year)
        }
        val expenseDiffered = async(Dispatchers.Default) {
            annualCashFlow(expenses, year)
        }
        val incomeCategoryDiffered = async(Dispatchers.Default) {
            annualTotalByCategory(incomes, year)
        }
        val expenseCategoryDiffered = async(Dispatchers.Default) {
            annualTotalByCategory(expenses, year)
        }

        // Get final values and calculate total
        val income = incomeDiffered.await()
        val expense = expenseDiffered.await()
        val balance = income.sum() - expense.sum()

        val balanceLevel = DoubleArray(income.size)
        income.forEachIndexed { index, transaction ->
            balanceLevel[index] = transaction - (expense.getOrNull(index) ?: 0.0)
        }

        val incomeCategories = incomeCategoryDiffered
            .await()
            .map { (order, amount) ->
                val category = when (order) {
                    IncomeCategory.Salary.ordinal -> IncomeCategory.Salary
                    IncomeCategory.Bonus.ordinal -> IncomeCategory.Bonus
                    IncomeCategory.Commission.ordinal -> IncomeCategory.Commission
                    IncomeCategory.Benefit.ordinal -> IncomeCategory.Benefit
                    IncomeCategory.Investment.ordinal -> IncomeCategory.Investment
                    else -> IncomeCategory.Other
                }
                Pair(
                    FinanceCategory.Income(category).toUiText(),
                    amount
                )
            }
            .sortedByDescending { it.second }

        val expenseCategories = expenseCategoryDiffered
            .await()
            .map { (order, amount) ->
                val category = when (order) {
                    ExpenseCategory.Housing.ordinal -> ExpenseCategory.Housing
                    ExpenseCategory.Utility.ordinal -> ExpenseCategory.Utility
                    ExpenseCategory.Food.ordinal -> ExpenseCategory.Food
                    ExpenseCategory.Transport.ordinal -> ExpenseCategory.Transport
                    ExpenseCategory.Health.ordinal -> ExpenseCategory.Health
                    ExpenseCategory.Education.ordinal -> ExpenseCategory.Education
                    ExpenseCategory.Entertainment.ordinal -> ExpenseCategory.Entertainment
                    else -> ExpenseCategory.Other
                }
                Pair(
                    FinanceCategory.Expense(category).toUiText(),
                    amount
                )
            }
            .sortedByDescending { it.second }

        _state.update {
            it.copy(
                isLoading = false,
                dataAvailable = income.sum() > 0 || expense.sum() > 0,
                balance = balance,
                incomeTransactions = income,
                expenseTransactions = expense,
                balanceLevel = balanceLevel.toList(),
                incomeCategories = incomeCategories,
                expenseCategories = expenseCategories
            )
        }
    }
}