package com.jd.financeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.jd.financeflow.ui.theme.FinanceFlowTheme
import com.jd.financeflow.features.finance.presentation.home.HomeScreen
import com.jd.financeflow.features.finance.presentation.home.HomeViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeActions
import com.jd.financeflow.features.finance.presentation.transactionDetails.TransactionDetailsScreen
import com.jd.financeflow.features.finance.presentation.transactionDetails.TransactionDetailsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val homeViewModel: HomeViewModel = koinViewModel()
                    val editTransactionViewModel: TransactionDetailsViewModel = koinViewModel(
                        parameters = { parametersOf(null) }
                    )

                    HomeScreen(
                        viewModel = homeViewModel,
                        addTransactionContent = {
                            TransactionDetailsScreen(
                                viewModel = editTransactionViewModel,
                                onSuccessfulAdd = {
                                    homeViewModel.onAction(
                                        HomeActions.RefreshBalance()
                                    )
                                    homeViewModel.onAction(
                                        HomeActions.ShowBottomSheet(false)
                                    )
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(vertical = 8.dp)
                                    .fillMaxSize()
                            )
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}



