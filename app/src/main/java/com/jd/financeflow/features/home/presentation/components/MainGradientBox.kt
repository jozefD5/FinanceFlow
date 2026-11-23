package com.jd.financeflow.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import com.jd.financeflow.ui.theme.baseDarkBlue
import com.jd.financeflow.ui.theme.topLightCyan
import com.jd.financeflow.ui.theme.midBrightBlue
import com.jd.financeflow.ui.theme.bottomTeal

/**
 * Main gradient box composable, used to display main color palette (theme)
 *
 * @param content content to be displayed inside the box
 * @param modifier modifier to be applied
 */
@Composable
fun MainGradientBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(baseDarkBlue)
            .drawBehind {
                val width = size.width
                val height = size.height

                // Glow 1: Vibrant Top-Right Cyan/Teal Aura
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            topLightCyan.copy(alpha = 0.65f),
                            Color.Transparent
                        ),
                        center = Offset(x = width * 0.9f, y = height * 0.05f),
                        radius = width * 0.8f
                    )
                )

                // Glow 2: Deep Middle-Left Blue Core (creates the rich contrast)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            midBrightBlue.copy(alpha = 0.5f),
                            Color.Transparent
                        ) as List<Color>,
                        center = Offset(x = width * 0.1f, y = height * 0.45f),
                        radius = width * 1.1f
                    )
                )

                // Glow 3: Bright Bottom-Right Atmospheric Teal
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            bottomTeal.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(x = width * 0.85f, y = height * 0.75f),
                        radius = width * 0.9f
                    )
                )
            }
    ) {
        content()
    }
}