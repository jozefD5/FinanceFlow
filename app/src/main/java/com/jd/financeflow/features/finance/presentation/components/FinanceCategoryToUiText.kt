package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.core.presentation.UiText

/**
 * Converts a [FinanceCategory] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [FinanceCategory] value.
 */
fun FinanceCategory.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceId()
    )
}

/**
 * Converts a [FinanceCategory] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [FinanceCategory] value.
 */
fun FinanceCategory.typeToUiText(): UiText {
    return UiText.StringResource(
        id = this.typeToStringResourceId()
    )
}

/**
 * Converts a [FinanceCategory] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceCategory] value.
 */
private fun FinanceCategory.toStringResourceId(): Int {
    return when (this) {
        FinanceCategory.Income.Salary -> R.string.finance_category_salary
        FinanceCategory.Income.Bonus -> R.string.finance_category_bonus
        FinanceCategory.Income.Commissions -> R.string.finance_category_commissions
        FinanceCategory.Income.Benefits -> R.string.finance_category_benefits
        FinanceCategory.Income.Investments -> R.string.finance_category_investments
        FinanceCategory.Income.Other -> R.string.finance_category_other

        FinanceCategory.Expense.Utility -> R.string.finance_category_utility
        FinanceCategory.Expense.Housing -> R.string.finance_category_housing
        FinanceCategory.Expense.Transport -> R.string.finance_category_transport
        FinanceCategory.Expense.Food -> R.string.finance_category_food
        FinanceCategory.Expense.Entertainment -> R.string.finance_category_entertainment
        FinanceCategory.Expense.Health -> R.string.finance_category_health
        FinanceCategory.Expense.Education -> R.string.finance_category_education
        FinanceCategory.Expense.Other -> R.string.finance_category_other
    }
}

/**
 * Converts a [FinanceCategory] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceCategory] value.
 */
private fun FinanceCategory.typeToStringResourceId(): Int {
    return when (this) {
        is FinanceCategory.Income -> R.string.finance_type_income
        is FinanceCategory.Expense -> R.string.finance_type_expense
    }
}