package com.jd.financeflow.features.core.presentation.composableViewModelScope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner

/**
 * Utility composable design to provide scoped ViewModelStoreOwner and state preserving
 * SavableStateHolder.
 *
 * @param key key to use for the ViewModelStoreOwner
 * @param savableStateHolder savable state holder to use
 */
@Composable
fun ComposableViewModelScope(
    key: Any,
    savableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    content: @Composable () -> Unit
) {
    savableStateHolder.SaveableStateProvider(key) {
        val storeOwner = rememberViewModelStoreOwner()
        CompositionLocalProvider(
            value = LocalViewModelStoreOwner provides storeOwner,
            content = content
        )
    }
}