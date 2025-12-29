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
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsScreen
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val homeViewModel: HomeViewModel = koinViewModel()
            val editTransactionViewModel: FinanceDetailsViewModel = koinViewModel(
                parameters = { parametersOf(null) }
            )

            FinanceFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        viewModel = homeViewModel,
                        addTransactionContent = {
                            FinanceDetailsScreen(
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
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                    .padding(horizontal = 8.dp)
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



