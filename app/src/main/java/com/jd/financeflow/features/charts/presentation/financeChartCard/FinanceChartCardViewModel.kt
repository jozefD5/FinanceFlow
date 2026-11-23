package com.jd.financeflow.features.charts.presentation.financeChartCard

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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.charts.domain.ChartsRepository
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.core.domain.models.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.domain.models.finance.IncomeCategory
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.annualCashFlow
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.annualTotalByCategory
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.monthlyCashFlow
import com.jd.financeflow.features.core.presentation.components.financeMappers.toUiText
import com.jd.financeflow.features.core.presentation.financeCashFlowHelpers.monthlyTotalByCategoryRange


/**
 * Finance chart card view model
 *
 * @param repository Finance repository
 * @param startTimeMs Start time in milliseconds
 * @param interval Interval of the chart
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 */
class FinanceChartCardViewModel(
    private val repository: ChartsRepository,
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
            is FinanceChartCardActions.OnClearError -> _state.update { it.copy(error = null) }
            is FinanceChartCardActions.OnRefresh -> refreshChart()
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

        try {
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

            // Get grouped results from the coroutine. Expected grouped result is ((income, expense), (incomeCategories, expenseCategories))
            val (a1, a2) = withContext(Dispatchers.Default) {
                // Convert start time to year and month
                val zonedDateTime = Instant.ofEpochMilli(startTimeMs).atZone(ZoneId.systemDefault())
                val year = zonedDateTime.year
                val month = zonedDateTime.monthValue

                // Calculate key values asynchronously
                val incomeDiffered = async {
                    monthlyCashFlow(
                        items = incomes,
                        year = year,
                        month = month
                    )
                }
                val expenseDiffered = async {
                    monthlyCashFlow(
                        items = expenses,
                        year = year,
                        month = month
                    )
                }
                val incomeCategoryDiffered = async {
                    monthlyTotalByCategoryRange(
                        items = incomes,
                        year = year,
                        month = month
                    )
                }
                val expenseCategoryDiffered = async {
                    monthlyTotalByCategoryRange(
                        items = expenses,
                        year = year,
                        month = month
                    )
                }

                val incomeCategoriesResult = incomeCategoryDiffered
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

                val expenseCategoriesResult = expenseCategoryDiffered
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

                val pairA = Pair(
                    incomeDiffered.await(),
                    expenseDiffered.await(),
                )
                val pairB = Pair(
                    incomeCategoriesResult,
                    expenseCategoriesResult,
                )
                Pair(pairA, pairB)
            }
            val finalBalance = a1.first.sum() - a1.second.sum()

            var balanceRecord = 0.0
            val balanceLevel = DoubleArray(a1.first.size)
            a1.first.forEachIndexed { index, transaction ->
                val result = (transaction + balanceRecord) - (a1.second.getOrNull(index) ?: 0.0)
                balanceLevel[index] = result
                balanceRecord = result
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    dataAvailable = a1.first.sum() > 0 || a1.second.sum() > 0,
                    balance = finalBalance,
                    incomeTransactions = a1.first,
                    expenseTransactions = a1.second,
                    balanceLevel = balanceLevel.toList(),
                    incomeCategories = a2.first,
                    expenseCategories = a2.second
                )
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(isLoading = false, error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Calculate annual interval data
     */
    private fun calculateAnnualInterval() = viewModelScope.launch {
        try {
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


            // Get grouped results from the coroutine. Expected grouped result is ((income, expense), (incomeCategories, expenseCategories))
            val (a1, a2) = withContext(Dispatchers.Default) {
                // Convert start time to year and month
                val zonedDateTime = Instant.ofEpochMilli(startTimeMs).atZone(ZoneId.systemDefault())
                val year = zonedDateTime.year

                // Calculate key values asynchronously
                val incomeDiffered = async {
                    annualCashFlow(incomes, year)
                }
                val expenseDiffered = async {
                    annualCashFlow(expenses, year)
                }
                val incomeCategoryDiffered = async {
                    annualTotalByCategory(incomes, year)
                }
                val expenseCategoryDiffered = async {
                    annualTotalByCategory(expenses, year)
                }

                val incomeCategoriesResult = incomeCategoryDiffered
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
                        Pair(FinanceCategory.Income(category).toUiText(), amount)
                    }
                    .sortedByDescending { it.second }

                val expenseCategoriesResult = expenseCategoryDiffered
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
                        Pair(FinanceCategory.Expense(category).toUiText(), amount)
                    }
                    .sortedByDescending { it.second }

                val pairA = Pair(
                    incomeDiffered.await(),
                    expenseDiffered.await(),
                )
                val pairB = Pair(
                    incomeCategoriesResult,
                    expenseCategoriesResult,
                )

                Pair(pairA, pairB)
            }

            val balance = a1.first.sum() - a1.second.sum()
            val balanceLevel = DoubleArray(a1.first.size)
            a1.first.forEachIndexed { index, transaction ->
                balanceLevel[index] = transaction - (a1.second.getOrNull(index) ?: 0.0)
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    dataAvailable = a1.first.sum() > 0 || a1.second.sum() > 0,
                    balance = balance,
                    incomeTransactions = a1.first,
                    expenseTransactions = a1.second,
                    balanceLevel = balanceLevel.toList(),
                    incomeCategories = a2.first,
                    expenseCategories = a2.second
                )
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(isLoading = false, error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }
}