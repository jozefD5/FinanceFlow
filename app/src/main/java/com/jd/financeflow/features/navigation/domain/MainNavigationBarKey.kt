package com.jd.financeflow.features.navigation.domain

import kotlinx.serialization.Serializable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

import com.jd.financeflow.R

/**
 * Sealed interface representing the navigation bar keys for the main navigation bar.
 */
sealed interface MainNavigationBarKey : NavKey {
    val icon: ImageVector
    val selectedIcon: ImageVector
    val labelResourceId: Int

    @Serializable
    data object MainTab : MainNavigationBarKey {
        override val icon: ImageVector = Icons.Outlined.Home
        override val selectedIcon: ImageVector = Icons.Filled.Home
        override val labelResourceId = R.string.home_tab_label
    }

    @Serializable
    data object FinanceListTab : MainNavigationBarKey {
        override val icon: ImageVector = Icons.AutoMirrored.Outlined.ViewList
        override val selectedIcon: ImageVector = Icons.AutoMirrored.Filled.ViewList
        override val labelResourceId = R.string.finances_tab_label
    }

    companion object {
        val items = listOf(
            MainTab,
            FinanceListTab
        )

        val stateSaver = Saver<MainNavigationBarKey, String>(
            save = { it::class.qualifiedName },
            restore = { qualifiedName ->
                items.firstOrNull { it::class.qualifiedName == qualifiedName } ?: MainTab
            }
        )
    }
}

/**
 * Sealed interface representing the navigation keys for the home screen.
 */
sealed interface HomeRootKey : NavKey {
    @Serializable
    data object HomeScreen : HomeRootKey

    @Serializable
    data object FinanceDetailsScreen : HomeRootKey
}

/**
 * Sealed interface representing the navigation keys for the finance list screen.
 */
sealed interface FinanceRootKey : NavKey {
    @Serializable
    data object FinanceListScreen : FinanceRootKey
}