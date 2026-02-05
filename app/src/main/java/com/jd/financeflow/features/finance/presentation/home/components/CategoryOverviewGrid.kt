package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jd.financeflow.R

import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

/**
 * Displays a grid of category overview cards.
 *
 * @param categories the list of categories to display
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @param onClicked the action to perform when a category is clicked
 * @param modifier the modifier to apply to the grid
 * @param color the background color of the cards
 * @param textColor the text color of the cards
 * @param lineGraphColor the color of the line graph
 * @param lineGraphMaxValue the maximum value of the line graph set to null to disable the line graph
 */
@Composable
fun CategoryOverviewGrid(
    categories: List<Pair<FinanceCategory, Double>>,
    currencyCode: String,
    onClicked: (FinanceCategory) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    lineGraphColor: Color = MaterialTheme.colorScheme.primary,
    lineGraphMaxValue: Double? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 144.dp),
        modifier = modifier
    ) {
        items(
            items = categories,
            key = { it.first.hashCode() }
        ) {
            CategoryOverviewCard(
                category = it.first,
                value = it.second,
                currencyCode = currencyCode,
                onClicked = { onClicked(it.first) },
                color = color,
                textColor = textColor,
                lineGraphColor = lineGraphColor,
                lineGraphMaxValue = lineGraphMaxValue,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryOverviewGridPreview() {
    val categories = listOf(
        Pair(
            FinanceCategory.Income(IncomeCategory.Salary),
            100_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Bonus),
            50_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Commission),
            40_000.0
        ),
        Pair(
            FinanceCategory.Income(IncomeCategory.Investment),
            10_000.0
        )
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            CategoryOverviewGrid(
                categories = categories,
                currencyCode = "EUR",
                onClicked = {},
                lineGraphColor = Color.Green,
                lineGraphMaxValue = 200_000.0,
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}
