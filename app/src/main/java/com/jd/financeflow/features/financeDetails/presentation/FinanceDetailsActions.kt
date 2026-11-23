package com.jd.financeflow.features.financeDetails.presentation

/**
 * Represents actions that can be performed on the finance transaction details screen.
 */
sealed interface FinanceDetailsActions {
    /** Action to clear any displayed error message. */
    class ClearError : FinanceDetailsActions

    /**
     * Action to validate and save the transaction.
     *
     * @property action optional callback invoked after the transaction is saved successfully
     */
    data class OnSaveFinance(val action: (() -> Unit)? = null) : FinanceDetailsActions

    /** Action triggered when the back navigation button is clicked. */
    object OnBackNavigationClicked : FinanceDetailsActions

    /**
     * Action to update the transaction title.
     *
     * @property title the new title
     */
    data class OnUpdateTitle(val title: String) : FinanceDetailsActions

    /**
     * Action to update the transaction description.
     *
     * @property description the new description
     */
    data class OnUpdateDescription(val description: String) : FinanceDetailsActions

    /**
     * Action to update the transaction amount. The raw input is sanitized and validated before
     * being stored in the state.
     *
     * @property amount the raw amount text entered by the user
     */
    data class OnUpdateAmount(val amount: String) : FinanceDetailsActions

    /**
     * Action to update the transaction start date.
     *
     * @property date the new start date in epoch milliseconds
     */
    data class OnUpdateStartDate(val date: Long) : FinanceDetailsActions

    /**
     * Action to update the transaction end date.
     *
     * @property date the new start date in epoch milliseconds
     */
    data class OnUpdateEndDate(val date: Long) : FinanceDetailsActions

    /**
     * Toggle enable/disable option to set finance interval end date
     */
    data object OnToggleEnableEndDate : FinanceDetailsActions

    /**
     * Action to update the recurrence interval of the transaction.
     *
     * @property financeIntervalIndex index into `FinanceInterval.entries`
     */
    data class OnUpdateFinanceInterval(val financeIntervalIndex: Int) : FinanceDetailsActions

    /**
     * Action to switch between income and expense. Resets the selected category.
     *
     * @property financeTypeIndex index into `FinanceType.entries`
     */
    data class OnUpdateFinanceType(val financeTypeIndex: Int) : FinanceDetailsActions

    /**
     * Action to update the category of the transaction.
     *
     * @property financeCategoryIndex index into `IncomeCategory.entries` or
     * `ExpenseCategory.entries`, depending on the selected finance type
     */
    data class OnUpdateFinanceCategory(val financeCategoryIndex: Int) : FinanceDetailsActions
}
