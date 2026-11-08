package com.jd.financeflow.features.home.presentation

/**
 * Sealed interface representing actions that can be performed in the home screen.
 */
sealed interface HomeActions {
    /** Action to clear any displayed error message. */
    data object OnClearError : HomeActions
    /** Action to refresh the balance and transactions. */
    data object OnRefreshBalance : HomeActions
    /** Action triggered when the add finance button is clicked. */
    data object OnAddFinanceClicked : HomeActions
    /** Action triggered when the all activity/transactions section is clicked. */
    data object OnAllActivityClicked : HomeActions
}