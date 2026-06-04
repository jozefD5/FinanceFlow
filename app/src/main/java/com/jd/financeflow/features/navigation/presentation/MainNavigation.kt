package com.jd.financeflow.features.navigation.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.jd.financeflow.features.finance.presentation.charts.ChartsViewModel
import com.jd.financeflow.features.finance.presentation.charts.ChartsScreen

import org.koin.core.parameter.parametersOf
import org.koin.androidx.compose.koinViewModel

import com.jd.financeflow.features.finance.presentation.home.HomeViewModel
import com.jd.financeflow.features.navigation.domain.FinanceRootKey
import com.jd.financeflow.features.navigation.domain.HomeRootKey
import com.jd.financeflow.features.navigation.domain.MainNavigationBarKey
import com.jd.financeflow.features.finance.presentation.financeList.FinanceListScreen
import com.jd.financeflow.features.finance.presentation.financeList.FinanceListViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeScreen
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsScreen
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsViewModel
import com.jd.financeflow.features.finance.presentation.financeListFilter.FinanceListFilterScreen
import com.jd.financeflow.features.finance.presentation.financeListFilter.FinanceListFilterViewModel
import com.jd.financeflow.features.navigation.domain.ChartsRootKey

/**
 * Main navigation for the application.
 *
 * @param modifier modifier to be applied
 */
@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    // Default currency code
    val currencyCode = "EUR"

    // Navigation back stacks for individual tabs
    val homeBackStack = rememberNavBackStack(HomeRootKey.HomeScreen)
    val financeListBackStack = rememberNavBackStack(FinanceRootKey.FinanceListScreen())
    val chartsBackStack = rememberNavBackStack(ChartsRootKey.Charts)

    // Current navigation bar key (selected tab/key)
    var currentBarKey by rememberSaveable(stateSaver = MainNavigationBarKey.stateSaver) {
        mutableStateOf(MainNavigationBarKey.MainTab)
    }

    // Get the current back stack based on the current navigation bar key
    val currentBackStack = when (currentBarKey) {
        is MainNavigationBarKey.MainTab -> homeBackStack
        is MainNavigationBarKey.FinanceListTab -> financeListBackStack
        is MainNavigationBarKey.ChartTab -> chartsBackStack
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
                            } else {
                                while (currentBackStack.size > 1) {
                                    currentBackStack.removeLastOrNull()
                                }
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
            onBack = {
                currentBackStack.removeLastOrNull()
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(tween(200))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            )
        ) { key ->
            when (key) {
                // Main screen destinations
                is HomeRootKey -> {
                    when (key) {
                        is HomeRootKey.HomeScreen -> {
                            NavEntry(key = key) {
                                val viewModel: HomeViewModel = koinViewModel()

                                HomeScreen(
                                    viewModel = viewModel,
                                    onAddFinanceClicked = {
                                        homeBackStack.add(HomeRootKey.FinanceDetailsScreen)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        is HomeRootKey.FinanceDetailsScreen -> {
                            NavEntry(key = key) {
                                val viewModel: FinanceDetailsViewModel = koinViewModel(
                                    parameters = { parametersOf(null, currencyCode) }
                                )

                                FinanceDetailsScreen(
                                    viewModel = viewModel,
                                    onSuccessfulAdd = {
                                        homeBackStack.removeLastOrNull()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                // Finance screen destinations
                is FinanceRootKey -> {
                    when (key) {
                        is FinanceRootKey.FinanceListScreen -> {
                            NavEntry(key = key) {
                                val viewModel: FinanceListViewModel = koinViewModel(
                                    parameters = {
                                        parametersOf(
                                            currencyCode,
                                            key.filterConfiguration
                                        )
                                    }
                                )

                                FinanceListScreen(
                                    viewModel = viewModel,
                                    onFinanceClicked = {
                                        financeListBackStack.add(
                                            FinanceRootKey.FinanceDetailsScreen(it)
                                        )
                                    },
                                    onAddFinanceClicked = {
                                        financeListBackStack.add(
                                            FinanceRootKey.FinanceDetailsScreen(null)
                                        )
                                    },
                                    onFilterClicked = {
                                        financeListBackStack.add(
                                            FinanceRootKey.FinanceFilterScreen(
                                                filterConfiguration = viewModel
                                                    .state.value.filterConfiguration
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        is FinanceRootKey.FinanceDetailsScreen -> {
                            NavEntry(key = key) {
                                val viewModel: FinanceDetailsViewModel = koinViewModel(
                                    parameters = { parametersOf(key.financeId, currencyCode) }
                                )

                                FinanceDetailsScreen(
                                    viewModel = viewModel,
                                    onSuccessfulAdd = {
                                        financeListBackStack.removeLastOrNull()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        is FinanceRootKey.FinanceFilterScreen -> {
                            NavEntry(key = key) {
                                val viewModel: FinanceListFilterViewModel = koinViewModel(
                                    parameters = {
                                        parametersOf(
                                            currencyCode,
                                            key.filterConfiguration
                                        )
                                    }
                                )

                                FinanceListFilterScreen(
                                    viewModel = viewModel,
                                    onApplyFilter = { config ->
                                        currentBackStack.clear()
                                        currentBackStack.add(
                                            FinanceRootKey.FinanceListScreen(config)
                                        )
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                // Chart screen destinations
                is ChartsRootKey -> {
                    when (key) {
                        is ChartsRootKey.Charts -> {
                            NavEntry(key = key) {
                                val viewModel: ChartsViewModel = koinViewModel(
                                    parameters = { parametersOf(currencyCode) }
                                )

                                ChartsScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                else -> throw IllegalArgumentException("Invalid NavKey: $key")
            }
        }
    }
}