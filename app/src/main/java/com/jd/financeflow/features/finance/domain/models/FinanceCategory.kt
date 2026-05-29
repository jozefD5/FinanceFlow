package com.jd.financeflow.features.finance.domain.models

/**
 * Represents the type of transaction, either `Income` or `Expense`.
 */
enum class FinanceType {
    INCOME,
    EXPENSE
}

/**
 * Defines the specific categories for `Income` transactions.
 * These categories help in classifying different sources of income.
 */
enum class IncomeCategory {
    Salary,
    Bonus,
    Commission,
    Benefit,
    Investment,
    Other
}

/**
 * Defines the specific categories for `Expenses` transactions.
 * These categories help in classifying different types of expenditures.
 */
enum class ExpenseCategory {
    Housing,
    Utility,
    Food,
    Transport,
    Health,
    Education,
    Entertainment,
    Other
}

/**
 * A sealed class representing the various categories for financial transactions.
 *
 * This class uses nested enums to define distinct categories for both income and expenses,
 * ensuring clear separation and organization of financial data.
 *
 * @see Income
 * @see Expense
 */
sealed class FinanceCategory {
    /**
     * Represents a category for income transactions.
     *
     * @param category The specific income category.
     */
    data class Income(
        val category: IncomeCategory,
        var type: FinanceType = FinanceType.INCOME
    ) : FinanceCategory()

    /**
     * Represents a category for expense transactions.
     *
     * @param category The specific expense category.
     */
    data class Expense(
        val category: ExpenseCategory,
        var type: FinanceType = FinanceType.EXPENSE
    ) : FinanceCategory()
}