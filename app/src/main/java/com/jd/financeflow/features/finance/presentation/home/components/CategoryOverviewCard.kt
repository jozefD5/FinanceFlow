package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.presentation.components.toUiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

/**
 * Displays a category overview with the given value and currency code.
 *
 * @param category the category to display
 * @param value the value to display
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @param onClicked the action to perform when the category is clicked
 * @param modifier the modifier to apply to the card
 * @param color the background color of the card
 * @param textColor the text color of the card
 * @param lineGraphColor the color of the line graph
 * @param lineGraphMaxValue the maximum value of the line graph, set to null to disable the line graph
 */
@Composable
fun CategoryOverviewCard(
    category: FinanceCategory,
    value: Double,
    currencyCode: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    lineGraphColor: Color = MaterialTheme.colorScheme.primary,
    lineGraphMaxValue: Double? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = modifier.combinedClickable(
            onClick = onClicked,
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.toUiText().asString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textColor,
                    ),
                    modifier = Modifier.alpha(0.8f)
                )

                Text(
                    text = formatCurrency(value, currencyCode),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                )

                Spacer(modifier = Modifier.padding(4.dp))

                if (lineGraphMaxValue != null) {
                    Canvas(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Calculate the canvas width and height based on the line graph max value
                        val canvasWidth = try {
                            val displayPercentage = value / lineGraphMaxValue

                            if (displayPercentage <= 0) {
                                0.0f
                            } else if (displayPercentage > lineGraphMaxValue) {
                                size.width
                            } else {
                                size.width * displayPercentage.toFloat()
                            }
                        } catch (_: ArithmeticException) {
                            0.0f
                        }

                        val canvasHeight = size.height

                        drawLine(
                            start = Offset(x = canvasWidth, y = 0f),
                            end = Offset(x = 0f, y = canvasHeight),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round,
                            color = lineGraphColor
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryOverviewCardPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            CategoryOverviewCard(
                category = FinanceCategory.Income(IncomeCategory.Salary),
                value = 80_000.0,
                currencyCode = "EUR",
                onClicked = {},
                lineGraphColor = Color.Green,
                lineGraphMaxValue = 100_000.0,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}