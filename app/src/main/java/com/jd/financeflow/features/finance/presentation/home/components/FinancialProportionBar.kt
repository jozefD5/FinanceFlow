package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.core.presentation.components.formatCurrency


/**
 * Data class for representing financial proportion bar data
 *
 * @param label label to be displayed
 * @param value Value of the category to display
 * @param maxValue Maximum value to be used as 100% mark for the line graph
 * @param color Color of the line graph
 * @param currencyCode Currency code to be used e.g. EUR for Euro or USD for US Dollar
 */
data class FinancialProportionBarData(
    val label: String,
    val value: Double,
    val maxValue: Double,
    val color: Color,
    val currencyCode: String,
)


/**
 * Displays a financial proportion bar card.
 *
 * @param data the data to display
 * @param modifier the modifier to apply to the card
 * @param textColor the text color of the card
 */
@Composable
fun FinancialProportionBar(
    data: FinancialProportionBarData,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val progress = (data.value / data.maxValue).toFloat().coerceIn(0f, 1f)
    val formattedPercentage = String.format(
        LocalLocale.current.platformLocale,
        "%.1f",
        progress * 100
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(data.color)
            )

            Text(
                text = data.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatCurrency(data.value, data.currencyCode),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
            )

            Text(
                text = "$formattedPercentage%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = data.color,
            trackColor = data.color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialProportionBarPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            FinancialProportionBar(
                data = FinancialProportionBarData(
                    label = "Salary",
                    value = 80_000.0,
                    maxValue = 100_000.0,
                    color = Color.Green,
                    currencyCode = "EUR"
                ),
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}