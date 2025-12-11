package com.jd.financeflow.features.finance.presentation.home

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.presentation.components.finance.toUiText
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository

/**
 * View model for the home screen.
 *
 * @param repository repository for finance transactions
 */
class HomeViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
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
            is HomeActions.RefreshBalance -> {
                refresh()
            }

            is HomeActions.ClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }

            is HomeActions.AddTransaction -> {}

            is HomeActions.ShowBottomSheet -> {
                _state.update {
                    it.copy(showBottomSheet = action.value)
                }
            }
        }
    }

    /**
     * Refreshes the balance of the user and updates the state accordingly.
     */
    private fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        runCatching {
            // Refresh balance
            when (val result = refreshBalance()) {
                is Result.Success -> {}
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

            // Refresh category preview
            FinanceType.entries.forEach { type ->
                when (val result = refreshCategoryPreview(type)) {
                    is Result.Success -> {}
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.error.toUiText()
                            )
                        }
                    }
                }

            }

            _state.update {
                it.copy(
                    isLoading = false,
                    error = null
                )
            }
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
     * Refreshes the balance of the user and updates the state accordingly.
     *
     * @return [EmptyResult] indicating the success or failure of the operation
     */
    private suspend fun refreshBalance(): EmptyResult<DataError.Local> {
        return runCatching {
            when (val result = repository.getFinances()) {
                is Result.Success -> {
                    val totalIncome = repository.getTotal(FinanceType.INCOME)
                    val totalExpenses = repository.getTotal(FinanceType.EXPENSE)

                    _state.update {
                        it.copy(
                            balance = totalIncome - totalExpenses,
                            income = totalIncome,
                            expense = totalExpenses
                        )
                    }
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    Result.Error(result.error)
                }
            }
        }.getOrElse {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Refreshes the category preview for the given type and updates the state accordingly.
     *
     * @param type type of the category preview to be refreshed
     * @return [EmptyResult] indicating the success or failure of the operation
     */
    private suspend fun refreshCategoryPreview(type: FinanceType): EmptyResult<DataError.Local> {
        return runCatching {
            val categoryList = mutableListOf<Pair<FinanceCategory, Double>>()

            // Get total for each category, for the selected type, and sort by value
            if (type == FinanceType.INCOME) {
                FinanceCategory.IncomeCategory.entries.forEach { category ->
                    val total = repository.getTotal(
                        FinanceType.INCOME,
                        FinanceCategory.Income(category)
                    )

                    categoryList.add(Pair(FinanceCategory.Income(category), total))
                }
            } else {
                FinanceCategory.ExpenseCategory.entries.forEach { category ->
                    val total = repository.getTotal(
                        FinanceType.EXPENSE,
                        FinanceCategory.Expense(category)
                    )

                    categoryList.add(Pair(FinanceCategory.Expense(category), total))
                }
            }

            val sortedCategories = categoryList
                .sortedByDescending { it.second }
                .take(4)

            // Update categor state
            if (type == FinanceType.INCOME) {
                _state.update {
                    it.copy(incomeCategoriesPreview = sortedCategories)
                }
            } else {
                _state.update {
                    it.copy(expenseCategoriesPreview = sortedCategories)
                }
            }

            Result.Success(Unit)
        }.getOrElse {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}