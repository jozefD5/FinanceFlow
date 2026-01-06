package com.jd.financeflow.features.navigation.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

import com.jd.financeflow.features.finance.presentation.home.HomeViewModel
import com.jd.financeflow.features.navigation.domain.FinanceRootKey
import com.jd.financeflow.features.navigation.domain.HomeRootKey
import com.jd.financeflow.features.navigation.domain.MainNavigationBarKey
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsScreen
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsViewModel
import com.jd.financeflow.features.finance.presentation.financeList.FinanceListScreen
import com.jd.financeflow.features.finance.presentation.financeList.FinanceListViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeActions
import com.jd.financeflow.features.finance.presentation.home.HomeScreen


@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    // Navigation back stacks for individual tabs
    val homeBackStack = rememberNavBackStack(HomeRootKey.HomeScreen)
    val financeListBackStack = rememberNavBackStack(FinanceRootKey.FinanceListScreen)

    // Current navigation bar key (selected tab/key)
    var currentBarKey by rememberSaveable(stateSaver = MainNavigationBarKey.stateSaver) {
        mutableStateOf(MainNavigationBarKey.MainTab)
    }

    // Get the current back stack based on the current navigation bar key
    val currentBackStack = when (currentBarKey) {
        is MainNavigationBarKey.MainTab -> homeBackStack
        is MainNavigationBarKey.FinanceListTab -> financeListBackStack
    }

    // Content of the main navigation bar
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                MainNavigationBarKey.items.forEach { key ->
                    NavigationBarItem(
                        selected = currentBarKey == key,
                        onClick = {
                            if (currentBarKey != key) {
                                currentBarKey = key
                            }
                        },
                        icon = {
                            val icon = if (currentBarKey == key) {
                                key.selectedIcon
                            } else {
                                key.icon
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(key.labelResourceId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = currentBackStack,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(tween(200))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            )
        ) { key ->
            when (key) {
                is HomeRootKey.HomeScreen -> {
                    NavEntry(key = key) {
                        val homeViewModel: HomeViewModel = koinViewModel()
                        val editTransactionViewModel: FinanceDetailsViewModel = koinViewModel(
                            parameters = { parametersOf(null) }
                        )

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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is FinanceRootKey.FinanceListScreen -> {
                    NavEntry(key = key) {
                        val financeListViewModel: FinanceListViewModel = koinViewModel()

                        FinanceListScreen(
                            viewModel = financeListViewModel,
                            onClicked = {},
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> throw IllegalArgumentException("Invalid NavKey: $key")
            }
        }
    }
}