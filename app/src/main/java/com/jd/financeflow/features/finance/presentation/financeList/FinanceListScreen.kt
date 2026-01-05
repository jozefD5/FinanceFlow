package com.jd.financeflow.features.finance.presentation.financeList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.features.core.presentation.components.ErrorDialog
import com.jd.financeflow.ui.theme.primaryLightMediumContrast


//SearchBar(
//modifier = Modifier
//.align(Alignment.TopCenter)
//.semantics { traversalIndex = 0f },
//inputField = {
//    SearchBarDefaults.InputField(
//        query = textFieldState.text.toString(),
//        onQueryChange = { textFieldState.edit { replace(0, length, it) } },
//        onSearch = {
//            onSearch(textFieldState.text.toString())
//            expanded = false
//        },
//        expanded = expanded,
//        onExpandedChange = { expanded = it },
//        placeholder = { Text("Search") }
//    )
//},
//expanded = expanded,
//onExpandedChange = { expanded = it },
//)

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceListView(
    state: FinanceListState,
    onAction: (FinanceListActions) -> Unit,
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState = rememberSearchBarState()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.background(primaryLightMediumContrast)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 8.dp,
                    end = 8.dp
                )
        ) {
            SearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchQuery,
                        onQueryChange = {
                            onAction(FinanceListActions.UpdateSearchQuery(it))
                        },
                        onSearch = {
                            onAction(FinanceListActions.OnSearch())
                        },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text("Search") }
                    )
                },
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = { },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {}
    }

    if (state.error != null) {
        ErrorDialog(
            content = state.error.asString(),
            onConfirmation = { onAction(FinanceListActions.ClearError()) },
            onDismiss = { onAction(FinanceListActions.ClearError()) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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