package com.jd.financeflow.features.finance.presentation.charts

import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
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
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.annualCashFlow
import com.jd.financeflow.features.finance.presentation.components.annualTotalByCategory
import com.jd.financeflow.features.finance.presentation.components.avgMonthlyCashFlow
import com.jd.financeflow.features.finance.presentation.components.toUiText
import java.time.LocalDate

/**
 * Finance charts view model
 *
 * @param repository Finance repository
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 */
class ChartsViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(
        ChartsState(
            currencyCode = currencyCode,
            selectedMonthIndex = LocalDate.now().monthValue
        )
    )
    val state = _state
        .onStart {
            refreshCashFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
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

            is ChartsActions.OnRefresh -> {
                refreshCashFlow()
            }

            is ChartsActions.OnToggleChartOption -> {
                val updatedList = state.value.selectedChartOptions
                    .toMutableList()
                    .mapIndexed { index, value ->
                        if (index == action.index) !value else value
                    }
                _state.update {
                    it.copy(selectedChartOptions = updatedList)
                }
            }

            is ChartsActions.OnSelectYear -> {
                _state.update {
                    it.copy(selectedYearIndex = action.index)
                }
            }

            is ChartsActions.OnSelectMonth -> {
                _state.update {
                    it.copy(selectedMonthIndex = action.index)
                }
            }

            is ChartsActions.OnToggleBalanceRollover -> {
                _state.update {
                    it.copy(enableCascadeBalance = !it.enableCascadeBalance)
                }
            }
        }
    }

    /**
     * Calculate average monthly cash flow using incomes and expenses
     */
    private fun refreshCashFlow() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val currentState = _state.value


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

        // Monthly cash flow
        val avgMonthlyIncomeDeferred =
            async(Dispatchers.Default) {
                avgMonthlyCashFlow(
                    incomes,
                    currentState.selectedMonthIndex
                )
            }
        val avgMonthlyExpenseDeferred =
            async(Dispatchers.Default) {
                avgMonthlyCashFlow(
                    expenses,
                    currentState.selectedMonthIndex
                )
            }

        // Annual cash flow
        val annualIncomeDiffered =
            async(Dispatchers.Default) { annualCashFlow(incomes) }
        val annualExpenseDiffered =
            async(Dispatchers.Default) { annualCashFlow(expenses) }

        // Annual total by category
        val incomeCategoryTotalDeferred =
            async(Dispatchers.Default) { annualTotalByCategory(incomes) }
        val expenseCategoryTotalDeferred =
            async(Dispatchers.Default) { annualTotalByCategory(expenses) }

        // Final cash flow values
        val avgMonthlyIncome = avgMonthlyIncomeDeferred.await()
        val avgMonthlyExpense = avgMonthlyExpenseDeferred.await()

        var runningAvgBalance = 0.0
        val avgMonthlyBalance = avgMonthlyIncome.mapIndexed { index, income ->
            runningAvgBalance += income - (avgMonthlyExpense.getOrNull(index) ?: 0.0)
            runningAvgBalance
        }

        val annualIncome = annualIncomeDiffered.await()
        val annualExpense = annualExpenseDiffered.await()

        var runningAnnualBalance = 0.0
        val annualBalance = annualIncome.mapIndexed { index, income ->
            if (currentState.enableCascadeBalance) {
                runningAnnualBalance += income - (annualExpense.getOrNull(index) ?: 0.0)
                runningAnnualBalance
            } else {
                income - (annualExpense.getOrNull(index) ?: 0.0)
            }
        }

        // Categories
        val incomeCategoryTotal = incomeCategoryTotalDeferred.await()
        val expenseCategoryTotal = expenseCategoryTotalDeferred.await()

        val incomeCategories = incomeCategoryTotal
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

        val expenseCategories = expenseCategoryTotal
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

        // Update state
        _state.update {
            it.copy(
                isLoading = false,
                avgMonthlyIncome = avgMonthlyIncome,
                avgMonthlyExpense = avgMonthlyExpense,
                avgMonthlyBalance = avgMonthlyBalance,
                yearlyCashFlowIncome = annualIncome,
                yearlyCashFlowExpense = annualExpense,
                yearlyCashFlowBalance = annualBalance,
                incomeCategories = incomeCategories,
                expenseCategories = expenseCategories
            )
        }
    }
}
