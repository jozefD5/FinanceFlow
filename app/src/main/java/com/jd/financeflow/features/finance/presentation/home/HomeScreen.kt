package com.jd.financeflow.features.finance.presentation.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.features.finance.presentation.home.components.BalanceCard
import com.jd.financeflow.ui.theme.FinanceFlowTheme

/**
 * Screen for the home screen.
 *
 * @param modifier modifier to be applied
 * @param viewModel view model to be used
 * @param addTransactionContent content for the add transaction screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    addTransactionContent: @Composable () -> Unit = {}
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    HomeView(
        state = state.value,
        onAction = { viewModel.onAction(it) },
        modifier = modifier,
        addTransactionContent = addTransactionContent
    )
}

/**
 * View for the home screen.
 *
 * @param state state to be displayed
 * @param onAction action to be performed
 * @param modifier modifier to be applied
 * @param addTransactionContent content for the add transaction screen
 * @param scrollState scroll state to be used
 * @param sheetState sheet state to be used
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    state: HomeState,
    onAction: (HomeActions) -> Unit,
    modifier: Modifier = Modifier,
    addTransactionContent: @Composable () -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2781))
                .statusBarsPadding()
        ) {
            BalanceCard(
                balance = state.balance,
                income = state.income,
                expense = state.expense,
                currencyCode = state.currencyCode,
                color = Color.Transparent,
                textColor = Color.White,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            )

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {

                }
            }

            if (state.showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onAction(HomeActions.ShowBottomSheet(false)) },
                    sheetState = sheetState,
                ) {
                    addTransactionContent()
                }
            }
        }

        FloatingActionButton(
            onClick = { onAction(HomeActions.ShowBottomSheet(true)) },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    FinanceFlowTheme {
        Scaffold { innerPadding ->
            HomeView(
                state = HomeState(
                    showBottomSheet = false,
                    balance = 100_000.0,
                    income = 150_000.0,
                    expense = 50_000.0,
                ),
                onAction = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )
        }
    }
}