package com.jd.financeflow.features.finance.presentation.charts

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
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.presentation.components.annualCashFlow
import com.jd.financeflow.features.finance.presentation.components.avgMonthlyCashFlow

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
    private val _state = MutableStateFlow(ChartsState(currencyCode = currencyCode))
    val state = _state
        .onStart {
            refreshCashFlow()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChartsState(currencyCode = currencyCode)
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
        val avgMonthlyIncomeDeferred =
            async(Dispatchers.Default) { avgMonthlyCashFlow(incomes) }
        val avgMonthlyExpenseDeferred =
            async(Dispatchers.Default) { avgMonthlyCashFlow(expenses) }

        // Monthly average
        val annualIncomeDiffered =
            async(Dispatchers.Default) { annualCashFlow(incomes) }
        val annualExpenseDiffered =
            async(Dispatchers.Default) { annualCashFlow(expenses) }

        // Final values
        val avgMonthlyIncome = avgMonthlyIncomeDeferred.await()
        val avgMonthlyExpense = avgMonthlyExpenseDeferred.await()

        val annualIncome = annualIncomeDiffered.await()
        val annualExpense = annualExpenseDiffered.await()
        val annualBalance = annualIncome.mapIndexed { index, value ->
            value - (annualExpense.getOrNull(index) ?: 0.0)
        }

        _state.update {
            it.copy(
                isLoading = false,
                avgMonthlyIncome = avgMonthlyIncome,
                avgMonthlyExpense = avgMonthlyExpense,
                yearlyCashFlowIncome = annualIncome,
                yearlyCashFlowExpense = annualExpense,
                yearlyCashFlowBalance = annualBalance
            )
        }
    }
}
