package com.jd.financeflow.features.core.domain.finance

enum class FinanceType {
    INCOME,
    EXPENSE
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
    data class Income(val category: IncomeCategory) : FinanceCategory()
    data class Expense(val category: ExpenseCategory) : FinanceCategory()

    /**
     * Defines the specific categories for `FinanceType.Income` transactions.
     * These categories help in classifying different sources of income.
     */
    enum class IncomeCategory {
        Salary, Bonus, Commissions, Benefits, Investments, Other
    }

    /**
     * Defines the specific categories for `FinanceType.Expenses` transactions.
     * These categories help in classifying different types of expenditures.
     */
    enum class ExpenseCategory {
        Utility, Housing, Transport, Food, Entertainment, Health, Education, Other
    }
}