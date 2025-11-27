package com.jd.financeflow.features.finance.presentation.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * View for the home screen.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 */
@Composable
fun HomeView(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {

}