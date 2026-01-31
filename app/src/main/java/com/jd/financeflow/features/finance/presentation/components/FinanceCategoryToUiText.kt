package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

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
 * Converts a [FinanceCategory] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceCategory] value.
 */
private fun FinanceCategory.toStringResourceId(): Int {
    return when (this) {
        is FinanceCategory.Income -> {
            when (this.category) {
                IncomeCategory.Salary -> R.string.finance_category_salary
                IncomeCategory.Bonus -> R.string.finance_category_bonus
                IncomeCategory.Commission -> R.string.finance_category_commission
                IncomeCategory.Benefit -> R.string.finance_category_benefit
                IncomeCategory.Investment -> R.string.finance_category_investment
                IncomeCategory.Other -> R.string.finance_category_other
            }
        }

        is FinanceCategory.Expense -> {
            when (this.category) {
                ExpenseCategory.Housing -> R.string.finance_category_housing
                ExpenseCategory.Utility -> R.string.finance_category_utility
                ExpenseCategory.Transport -> R.string.finance_category_transport
                ExpenseCategory.Food -> R.string.finance_category_food
                ExpenseCategory.Entertainment -> R.string.finance_category_entertainment
                ExpenseCategory.Health -> R.string.finance_category_health
                ExpenseCategory.Education -> R.string.finance_category_education
                ExpenseCategory.Other -> R.string.finance_category_other

            }
        }
    }
}
