package com.jd.financeflow.features.navigation.domain

import kotlinx.serialization.Serializable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration

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
        override val icon: ImageVector = Icons.Outlined.TableRows
        override val selectedIcon: ImageVector = Icons.Filled.TableRows
        override val labelResourceId = R.string.finances_tab_label
    }

    @Serializable
    data object ChartTab : MainNavigationBarKey {
        override val icon: ImageVector = Icons.Outlined.Leaderboard
        override val selectedIcon: ImageVector = Icons.Filled.Leaderboard
        override val labelResourceId = R.string.charts_tab_label
    }

    companion object {
        val items = listOf(
            MainTab,
            FinanceListTab,
            ChartTab
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
    data class FinanceListScreen(
        val filterConfiguration: FilterConfiguration? = null
    ) : FinanceRootKey

    @Serializable
    data class FinanceDetailsScreen(val financeId: Int?) : FinanceRootKey

    @Serializable
    data class FinanceFilterScreen(
        val filterConfiguration: FilterConfiguration? = null
    ) : FinanceRootKey
}

/**
 * Sealed interface representing the navigation keys for the charts screen
 */
sealed interface ChartsRootKey : NavKey {
    @Serializable
    data object Charts : ChartsRootKey
}