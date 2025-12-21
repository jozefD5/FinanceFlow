package com.jd.financeflow.features.finance.domain.models


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
    sealed class Income : FinanceCategory() {
        object Salary : Income()
        object Bonus : Income()
        object Commissions : Income()
        object Benefits : Income()
        object Investments : Income()
        object Other : Income()
    }

    sealed class Expense : FinanceCategory() {
        object Housing : Expense()
        object Utility : Expense()
        object Food : Expense()
        object Transport : Expense()
        object Health : Expense()
        object Education : Expense()
        object Entertainment : Expense()
        object Other : Expense()
    }


//    data class Income(val category: IncomeCategory) : FinanceCategory()
//    data class Expense(val category: ExpenseCategory) : FinanceCategory()
//
//    /**
//     * Defines the specific categories for `FinanceType.Income` transactions.
//     * These categories help in classifying different sources of income.
//     */
//    enum class IncomeCategory(val value: Int) {
//        Salary(0),
//        Bonus(1),
//        Commissions(2),
//        Benefits(3),
//        Investments(4),
//        Other(5)
//    }
//
//    /**
//     * Defines the specific categories for `FinanceType.Expenses` transactions.
//     * These categories help in classifying different types of expenditures.
//     */
//    enum class ExpenseCategory(val value: Int) {
//        Housing(0),
//        Utility(1),
//        Food(2),
//        Transport(3),
//        Health(4),
//        Education(5),
//        Entertainment(6),
//        Other(7)
//    }
}