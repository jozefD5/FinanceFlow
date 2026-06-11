package com.jd.financeflow.features.finance.presentation.charts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Chart legend item for finance charts
 */
data class ChartLegendItem(
    val label: String,
    val color: Color
)

/**
 * Chart legend item grid for finance charts
 */
@Composable
fun ChartLegendItemGrid(
    data: List<ChartLegendItem>,
    modifier: Modifier = Modifier
) {
    val updateData = if (data.size % 3 == 0) {
        data
    } else {
        data + ChartLegendItem(label = "", color = Color.Transparent)
    }

    FlowRow(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        maxItemsInEachRow = 3,
        modifier = modifier
    ) {
        updateData.forEach {
            ChartLegendItemRow(
                color = it.color,
                label = it.label,
                modifier = Modifier
                    .widthIn(min = 64.dp)
            )
        }
    }
}

/**
 * Legend item for Finance charts
 *
 * @param color Color
 * @param label Label
 */
@Composable
private fun ChartLegendItemRow(
    color: Color,
    label: String,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        // Colored circle
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )

        // Label text
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun ChartLegendItemGridPreview() {
    val items = listOf(
        ChartLegendItem(label = "Salary", color = Color(0xFF9BCC69)),
        ChartLegendItem(label = "Investment", color = Color(0xFF8EBB6B)),
        ChartLegendItem(label = "Benefits", color = Color(0xFF0EE58E)),
        ChartLegendItem(label = "Commission", color = Color(0xFF6BBB98)),
        ChartLegendItem(label = "Other", color = Color(0xFF6E7720)),
    )

    MaterialTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ChartLegendItemGrid(
                data = items,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }
    }
}