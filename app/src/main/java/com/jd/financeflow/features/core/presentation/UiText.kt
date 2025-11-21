package com.jd.financeflow.features.core.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Sealed interface representing text to be displayed in the UI.
 */
sealed interface UiText {
    /**
     * Represents a dynamic string that can be displayed in the UI.
     * @property value The dynamic string value.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Represents a string resource that can be displayed in the UI.
     * @property id The resource ID.
     * @property args The arguments to format the string with.
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ) : UiText

    /**
     * Converts the [UiText] object to a string.
     * @return The string representation of the [UiText] object.
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id, args)
        }
    }
}