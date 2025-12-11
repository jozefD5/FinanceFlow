package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.presentation.components.finance.toUiText
import com.jd.financeflow.features.core.presentation.components.formatCurrency

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
 */
@Composable
fun CategoryOverview(
    category: FinanceCategory,
    value: Double,
    currencyCode: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        modifier = modifier.combinedClickable(
            onClick = onClicked,
            onLongClick = onClicked
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
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
                        color = textColor,
                    )
                )
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
fun CategoryOverviewPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            CategoryOverview(
                category = FinanceCategory.Income(FinanceCategory.IncomeCategory.Salary),
                value = 100_000.0,
                currencyCode = "EUR",
                onClicked = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}