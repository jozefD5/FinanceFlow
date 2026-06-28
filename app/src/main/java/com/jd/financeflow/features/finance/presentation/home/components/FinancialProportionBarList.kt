package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.ui.theme.FinanceFlowTheme


/**
 * Linear progress widget list
 *
 * @param items Items to display
 * @param modifier Modifier
 */
@Composable
fun LinearProgressWidgetList(
    items: List<FinancialProportionBarData>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        items.forEach { item ->
            key(item.label) {
                FinancialProportionBar(
                    data = item,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LinearProgressWidgetListPreview() {
    FinanceFlowTheme {
        LinearProgressWidgetList(
            items = listOf(
                FinancialProportionBarData(
                    label = "Salary",
                    value = 80_000.0,
                    maxValue = 100_000.0,
                    color = Color.Green,
                    currencyCode = "EUR"
                ),
                FinancialProportionBarData(
                    label = "Rent",
                    value = 20_000.0,
                    maxValue = 100_000.0,
                    color = Color.Red,
                    currencyCode = "EUR"
                ),
                FinancialProportionBarData(
                    label = "Food",
                    value = 10_000.0,
                    maxValue = 100_000.0,
                    color = Color.Blue,
                    currencyCode = "EUR"
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

