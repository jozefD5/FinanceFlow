package com.jd.financeflow.features.finance.presentation.transactionDetails

import java.util.Locale

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.domain.onError
import com.jd.financeflow.features.core.domain.onSuccess
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository


/**
 * View model for the transaction details screen. Responsible for adding new transaction or modifying
 * existing one if [transactionId] is provided.
 *
 * @param financeRepository repository for finance transactions
 * @param transactionId id of the transaction to be modified if not null
 */
class TransactionDetailsViewModel(
    private val financeRepository: FinanceRepository,
    private val transactionId: Int? = null
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionDetailsState())
    val state = _state
        .onStart {
            if (transactionId != null) {
                loadData(transactionId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionDetailsState()
        )

    /**
     * Handles actions for the add transaction screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: TransactionDetailsActions) {
        when (action) {
            is TransactionDetailsActions.ClearError -> {
                _state.update {
                    it.copy(
                        error = null
                    )
                }
            }

            is TransactionDetailsActions.SaveTransaction -> {
                saveTransaction(action.action)
            }

            is TransactionDetailsActions.UpdateTitle -> {
                _state.update {
                    it.copy(
                        title = action.title
                    )
                }
            }

            is TransactionDetailsActions.UpdateDescription -> {
                _state.update {
                    it.copy(
                        description = action.description
                    )
                }
            }

            is TransactionDetailsActions.UpdateAmount -> {
                validateAmount(action.amount)
            }

            is TransactionDetailsActions.UpdateFinanceInterval -> {
                _state.update {
                    it.copy(
                        financeIntervalIndex = action.financeIntervalIndex
                    )
                }
            }

            is TransactionDetailsActions.UpdateFinanceType -> {
                _state.update {
                    it.copy(
                        financeTypeIndex = action.financeTypeIndex,
                        financeCategoryIndex = 0
                    )
                }
            }

            is TransactionDetailsActions.UpdateFinanceCategory -> {
                _state.update {
                    it.copy(
                        financeCategoryIndex = action.financeCategoryIndex
                    )
                }
            }
        }
    }

    /**
     * Loads the transaction data if [transactionId] is provided.
     */

    private fun loadData(id: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            when (val result = financeRepository.getFinanceById(id)) {
                is Result.Success -> {
                    val categoryIndex = when (result.data.category) {
                        is FinanceCategory.Income -> {
                            FinanceCategory.IncomeCategory.entries.indexOf(result.data.category.category)
                        }

                        is FinanceCategory.Expense -> {
                            FinanceCategory.ExpenseCategory.entries.indexOf(result.data.category.category)
                        }
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            title = result.data.title,
                            description = result.data.description ?: "",
                            amount = String.format(
                                Locale.getDefault(),
                                "%.2f",
                                result.data.amount
                            ),
                            financeIntervalIndex = result.data.interval.ordinal,
                            financeTypeIndex = result.data.type.ordinal,
                            financeCategoryIndex = categoryIndex
                        )
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    }
                }
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
     * Updates and completes the private fun validateAmount function.
     *
     * This version separates concerns for better readability and testability:
     * 1. `sanitizeAmountInput`: Cleans the raw user input, allowing only digits and a single decimal.
     * 2. `formatAmountForDisplay`: Applies display-specific formatting (leading zeros, leading decimal).
     * 3. The main `validateAmount` function then orchestrates these,
     * and uses a refined regex for final validation before updating the state.
     */
    private fun validateAmount(amount: String) {
        try {
            val sanitizedValue = sanitizeAmountInput(amount)
            val formattedForDisplay = formatAmountForDisplay(sanitizedValue)

            // 3. Final validation against a comprehensive regex for valid numerical patterns
            // This regex ensures:
            // - Allows "0" or "0.XX"
            // - Allows positive integers "1" or "123"
            // - Allows positive decimals "1.XX" or "123.XX"
            // - Allows empty string for intermediate user input (e.g., clearing the field)
            // - Allows starting with "." for immediate conversion to "0."
            // - Limits to two decimal places
            val finalRegex = Regex("^(0(\\.\\d{0,2})?|[1-9]\\d*(\\.\\d{0,2})?|\\.\\d{0,2})?$")

            if (formattedForDisplay.matches(finalRegex) || formattedForDisplay.isEmpty()) {
                _state.update {
                    it.copy(
                        amount = formattedForDisplay,
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        error = DataError.Local.PARSING.toUiText()
                    )
                }
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    error = DataError.Local.PARSING.toUiText()
                )
            }
        }
    }

    /**
     * Sanitizes raw string input to ensure it contains only digits and at most one decimal point.
     * This is the first pass to clean user input.
     */
    private fun sanitizeAmountInput(input: String): String {
        val stringBuilder = StringBuilder()
        var hasDecimal = false
        for (char in input) {
            if (char.isDigit()) {
                stringBuilder.append(char)
            } else if (char == '.' && !hasDecimal) {
                stringBuilder.append(char)
                hasDecimal = true
            }
        }
        return stringBuilder.toString()
    }

    /**
     * Formats the sanitized amount string for display purposes, handling common input patterns
     * like leading zeros and leading decimal points.
     */
    private fun formatAmountForDisplay(sanitizedAmount: String): String {
        if (sanitizedAmount.isEmpty()) return ""

        var result = sanitizedAmount

        // Handle leading dot: ".5" becomes "0.5"
        if (result.startsWith(".")) {
            result = "0$result"
        }

        // Handle leading zero for non-decimal
        if (result.length > 1 && result.startsWith('0') && !result.startsWith("0.")) {
            result = result.dropWhile { it == '0' }.ifEmpty { "0" }
        }

        // Ensure only two decimal places are maintained during input
        val parts = result.split('.')
        if (parts.size == 2 && parts[1].length > 2) {
            result = "${parts[0]}.${parts[1].substring(0, 2)}"
        }
        return result
    }

    /**
     * Saves the transaction to the repository.
     *
     * @param onSuccessAction action to be performed on success if not null
     */
    private fun saveTransaction(onSuccessAction: (() -> Unit)? = null) = viewModelScope.launch {
        var invalidTitle = false
        var invalidAmount = false

        _state.update { it.copy(isLoading = true) }

        // Verify values
        if (_state.value.title.isBlank()) {
            invalidTitle = true
        }
        if (_state.value.amount.isBlank()) {
            invalidAmount = true
        }

        if (invalidTitle || invalidAmount) {
            _state.update {
                it.copy(
                    isLoading = false,
                    invalidTitle = invalidTitle,
                    invalidAmount = invalidAmount,
                    error = DataError.Local.INVALID_ARGUMENT.toUiText()
                )
            }
            return@launch
        }

        runCatching {
            val category = if (_state.value.financeTypeIndex == FinanceType.INCOME.ordinal) {
                FinanceCategory.Income(
                    FinanceCategory.IncomeCategory.entries[_state.value.financeCategoryIndex]
                )
            } else {
                FinanceCategory.Expense(
                    FinanceCategory.ExpenseCategory.entries[_state.value.financeCategoryIndex]
                )
            }

            val finance = Finance(
                title = _state.value.title,
                description = _state.value.description,
                amount = _state.value.amount.toDouble(),
                interval = FinanceInterval.entries[_state.value.financeIntervalIndex],
                type = FinanceType.entries[_state.value.financeTypeIndex],
                category = category
            )

            val result = if (transactionId == null) {
                financeRepository.insertFinance(finance)
            } else {
                financeRepository.updateFinance(finance)
            }

            result
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            invalidTitle = false,
                            invalidAmount = false,
                            title = "",
                            description = "",
                            amount = "",
                            financeIntervalIndex = 0,
                            financeTypeIndex = 0,
                            financeCategoryIndex = 0
                        )
                    }
                    onSuccessAction?.invoke()
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.toUiText()
                        )
                    }
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
}