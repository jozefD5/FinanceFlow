package com.jd.financeflow.features.navigation.presentation

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

import com.jd.financeflow.features.navigation.domain.MainNavigationBarKey

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    //val backStack = rememberNavBackStack(MainNavigationKey.MainScreen)

    var currentBarKey by rememberSaveable(stateSaver = MainNavigationBarKey.stateSaver) {
        mutableStateOf(MainNavigationBarKey.MainTab)
    }

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
                                contentDescription = key.label
                            )
                        },
                        label = { Text(key.label) }
                    )
                }
            }
        }
    ) {
        when (currentBarKey) {
            is MainNavigationBarKey.MainTab -> {
                Text("Home")
            }

            is MainNavigationBarKey.FinanceListTab -> {
                Text("Finances")
            }
        }
    }
}