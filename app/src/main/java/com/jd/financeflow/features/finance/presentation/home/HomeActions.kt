package com.jd.financeflow.features.finance.presentation.home

/**
 * Sealed interface representing actions that can be performed in the home screen.
 */
sealed interface HomeActions {
    object ClearError : HomeActions
    object AddTransaction : HomeActions
}