package com.jd.financeflow.features.core.presentation.components

import kotlin.math.absoluteValue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Available/supported colors for avatar
private val colors = listOf(
    Color(0xFFB3E5FC), // Light Blue
    Color(0xFFFFF9C4), // Light Yellow
    Color(0xFFC8E6C9), // Light Green
    Color(0xFFFFCCBC), // Light Orange
    Color(0xFFD1C4E9), // Light Purple
    Color(0xFFFFCDD2)  // Light Red
)

/**
 * Displays a circle avatar with the initial of the given name.
 *
 * @param name the name to display the initial of
 * @param modifier the modifier to apply to the avatar
 * @param size the size of the avatar
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircleInitialAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val initialFirst = name.trim().firstOrNull()?.uppercase() ?: ""
    val initialSecond = name.trim().getOrNull(1)?.uppercase() ?: ""
    val background = getColorFromName(name)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(background.copy(alpha = 0.2f))
    ) {
        Text(
            text = "$initialFirst$initialSecond",
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Gets a color from the given name.
 *
 * @param name the name to get the color from
 * @return the color
 */
private fun getColorFromName(name: String): Color {
    val index = (name.hashCode().absoluteValue) % colors.size
    return colors[index]
}

@Preview
@Composable
fun CircleInitialAvatarPreview() {
    CircleInitialAvatar(name = "Joe Joe")
}