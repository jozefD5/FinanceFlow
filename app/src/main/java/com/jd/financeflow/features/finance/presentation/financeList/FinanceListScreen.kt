package com.jd.financeflow.features.finance.presentation.financeList

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

/**
 * Screen for displaying a list of finance transactions.
 *
 * @param modifier modifier to be applied
 * @param onClicked callback for when an item is clicked
 * @param viewModel view model to be used
 */
@Composable
fun FinanceListScreen(
    modifier: Modifier = Modifier,
    onClicked: (id: Int) -> Unit = {},
    viewModel: FinanceListViewModel = koinViewModel()
) {
}

/**
 * View for displaying a list of finance transactions.
 *
 * @param state state to be displayed
 * @param onAction action to be performed when item is clicked
 * @param modifier modifier to be applied
 */
@Composable
fun FinanceListView(
    state: FinanceListState,
    onAction: (FinanceListActions) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {

}

@Preview(showBackground = true)
@Composable
fun FinanceListViewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceListView(
                state = FinanceListState(),
                onAction = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}