package com.jd.financeflow.features.financeDetails.presentation

import java.util.Locale
import java.util.UUID

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CancellationException
import kotlin.text.iterator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.result.onError
import com.jd.financeflow.features.core.domain.result.onSuccess
import com.jd.financeflow.features.core.domain.models.finance.Finance
import com.jd.financeflow.features.core.domain.models.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.models.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.models.finance.IncomeCategory
import com.jd.financeflow.features.core.domain.models.finance.FinanceType
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.financeDetails.domain.FinanceDetailsRepository


/**
 * View model for the finance transaction details screen. Responsible for adding new transaction or modifying
 * existing one if [financeId] is provided.
 *
 * @param repository repository for finance transactions
 * @param financeId finance transaction to be modified or null for new transaction
 * @param currencyCode ISO currency code used for displaying the amount, e.g. "EUR"
 */
class FinanceDetailsViewModel(
    private val repository: FinanceDetailsRepository,
    private val financeId: Int? = null,
    private val currencyCode: String = "EUR"
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceDetailsState(currencyCode = currencyCode))

    /**
     * State flow representing the UI state of the finance details screen. When [financeId] is
     * provided, the existing transaction is loaded once collection starts.
     */
    val state = _state
        .onStart {
            if (financeId != null) {
                loadExistingFinanceTransaction(financeId)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    /**
     * Handles actions for the add finance details screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: FinanceDetailsActions) {
        when (action) {
            is FinanceDetailsActions.ClearError -> _state.update { it.copy(error = null) }

            is FinanceDetailsActions.OnSaveFinance -> saveFinance(action.action)

            is FinanceDetailsActions.OnBackNavigationClicked -> {}

            is FinanceDetailsActions.OnUpdateTitle -> _state.update { it.copy(title = action.title) }

            is FinanceDetailsActions.OnUpdateDescription -> _state.update { it.copy(description = action.description) }

            is FinanceDetailsActions.OnUpdateAmount -> validateAmount(action.amount)

            is FinanceDetailsActions.OnUpdateStartDate -> _state.update { it.copy(startDate = action.startDate) }

            is FinanceDetailsActions.OnUpdateFinanceInterval -> _state.update {
                it.copy(
                    financeIntervalIndex = action.financeIntervalIndex
                )
            }

            is FinanceDetailsActions.OnUpdateFinanceType -> _state.update {
                it.copy(
                    financeTypeIndex = action.financeTypeIndex,
                    financeCategoryIndex = 0
                )
            }

            is FinanceDetailsActions.OnUpdateFinanceCategory -> _state.update {
                it.copy(
                    financeCategoryIndex = action.financeCategoryIndex
                )
            }
        }
    }

    /**
     * Loads existing finance transaction from the repository and maps it into the form state,
     * resolving the type and category indices from its [FinanceCategory].
     *
     * @param id id of the finance transaction
     * @return the launched [kotlinx.coroutines.Job]
     */
    private fun loadExistingFinanceTransaction(id: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        try {
            when (val result = repository.getFinanceById(id)) {
                is Result.Success -> {
                    // Get index of the selected type and category
                    val typeIndex = when (result.data.category) {
                        is FinanceCategory.Income -> FinanceType.INCOME.ordinal
                        is FinanceCategory.Expense -> FinanceType.EXPENSE.ordinal
                    }

                    val categoryIndex = when (result.data.category) {
                        is FinanceCategory.Income -> IncomeCategory.entries.indexOf(result.data.category.category)
                        is FinanceCategory.Expense -> ExpenseCategory.entries.indexOf(result.data.category.category)
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
                            startDate = result.data.timeStampMs,
                            financeIntervalIndex = result.data.interval.ordinal,
                            financeTypeIndex = typeIndex,
                            financeCategoryIndex = categoryIndex
                        )
                    }
                }

                is Result.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.error.toUiText()
                    )
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.update { it.copy(isLoading = false, error = DataError.Local.UNKNOWN.toUiText()) }
        }
    }

    /**
     * Sanitizes, formats and validates the raw amount input and updates [FinanceDetailsState.amount].
     *
     * The input is processed in steps:
     * 1. [sanitizeAmountInput] keeps only digits and a single decimal point.
     * 2. [formatAmountForDisplay] normalises leading zeros and a leading decimal point and limits
     * the value to two decimal places.
     * 3. The result is validated against a regex for non-negative amounts with up to two decimals.
     *
     * If validation fails, [FinanceDetailsState.error] is set to a parsing error instead.
     *
     * @param amount raw amount text entered by the user
     */
    private fun validateAmount(amount: String) {
        try {
            val sanitizedValue = sanitizeAmountInput(amount)
            val formattedForDisplay = formatAmountForDisplay(sanitizedValue)

            // Final validation against a comprehensive regex for valid numerical patterns
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
                _state.update { it.copy(error = DataError.Local.PARSING.toUiText()) }
            }
        } catch (_: Exception) {
            _state.update { it.copy(error = DataError.Local.PARSING.toUiText()) }
        }
    }

    /**
     * Sanitizes raw string input to ensure it contains only digits and at most one decimal point.
     * This is the first pass to clean user input.
     *
     * @param input raw user input
     * @return input stripped of all characters except digits and the first decimal point
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
     * like leading zeros and leading decimal points, and truncates to two decimal places.
     *
     * @param sanitizedAmount amount already cleaned by [sanitizeAmountInput]
     * @return amount formatted for display, or an empty string for empty input
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
     * Validates the form and saves the finance to the repository.
     *
     * A blank title or amount marks the corresponding field as invalid and aborts the save. Otherwise
     * a [Finance] is built from the current state and inserted when [financeId] is null, or updated
     * when editing an existing transaction.
     *
     * @param onSuccessAction action to be performed on success if not null
     * @return the launched [kotlinx.coroutines.Job]
     */
    private fun saveFinance(onSuccessAction: (() -> Unit)? = null) = viewModelScope.launch {
        val currentState = _state.value

        var invalidTitle = false
        var invalidAmount = false

        _state.update { it.copy(isLoading = true) }

        // Verify values
        if (currentState.title.isBlank()) {
            invalidTitle = true
        }
        if (currentState.amount.isBlank()) {
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
            val category = if (currentState.financeTypeIndex == FinanceType.INCOME.ordinal) {
                FinanceCategory.Income(
                    IncomeCategory.entries[currentState.financeCategoryIndex]
                )
            } else {
                FinanceCategory.Expense(
                    ExpenseCategory.entries[currentState.financeCategoryIndex]
                )
            }

            val finance = Finance(
                id = financeId ?: UUID.randomUUID().hashCode(),
                title = currentState.title,
                description = currentState.description,
                amount = currentState.amount.toDouble(),
                timeStampMs = currentState.startDate,
                interval = FinanceInterval.entries[currentState.financeIntervalIndex],
                category = category,
                createdAt = System.currentTimeMillis()
            )

            val result = if (financeId == null) {
                repository.insertFinance(finance)
            } else {
                repository.updateFinance(finance)
            }

            result
                .onSuccess {
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