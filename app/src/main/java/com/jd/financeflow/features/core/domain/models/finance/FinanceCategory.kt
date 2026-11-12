package com.jd.financeflow.features.core.domain.models.finance

/**
 * Represents the type of transaction, either [EXPENSE] or [INCOME].
 */
enum class FinanceType {
    /** Expense transaction type. */
    EXPENSE,
    /** Income transaction type. */
    INCOME
}

/**
 * Defines the specific categories for `Income` transactions.
 * These categories help in classifying different sources of income.
 */
enum class IncomeCategory {
    /** Salary income category. */
    Salary,
    /** Bonus income category. */
    Bonus,
    /** Commission income category. */
    Commission,
    /** Benefit income category. */
    Benefit,
    /** Investment income category. */
    Investment,
    /** Other income category. */
    Other
}

/**
 * Defines the specific categories for `Expenses` transactions.
 * These categories help in classifying different types of expenditures.
 */
enum class ExpenseCategory {
    /** Housing expense category. */
    Housing,
    /** Utility expense category. */
    Utility,
    /** Food expense category. */
    Food,
    /** Transport expense category. */
    Transport,
    /** Health expense category. */
    Health,
    /** Education expense category. */
    Education,
    /** Entertainment expense category. */
    Entertainment,
    /** Other expense category. */
    Other
}

/**
 * A sealed class representing the various categories for financial transactions.
 *
 * This class uses nested subclasses to define distinct categories for both income and expenses,
 * ensuring clear separation and organization of financial data.
 *
 * @property type The type of finance transaction ([FinanceType]).
 * @property order The sorting or display order index.
 * @see Income
 * @see Expense
 */
sealed class FinanceCategory(
    val type: FinanceType,
    val order: Int,
) {
    /**
     * Represents a category for income transactions.
     *
     * @property category The specific income category ([IncomeCategory]).
     */
    data class Income(val category: IncomeCategory) :
        FinanceCategory(FinanceType.INCOME, category.ordinal)

    /**
     * Represents a category for expense transactions.
     *
     * @property category The specific expense category ([ExpenseCategory]).
     */
    data class Expense(
        val category: ExpenseCategory
    ) : FinanceCategory(FinanceType.EXPENSE, category.ordinal)
}