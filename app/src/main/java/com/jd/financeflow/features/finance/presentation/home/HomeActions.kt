package com.jd.financeflow.features.finance.presentation.home

/**
 * Sealed interface representing actions that can be performed in the home screen.
 */
sealed interface HomeActions {
    class ClearError : HomeActions
    class RefreshBalance : HomeActions
    class AddTransactionClicked : HomeActions
    data class ShowBottomSheet(val value: Boolean) : HomeActions
}