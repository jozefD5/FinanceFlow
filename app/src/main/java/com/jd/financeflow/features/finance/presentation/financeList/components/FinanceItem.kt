package com.jd.financeflow.features.finance.presentation.financeList.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory

private val incomeColors = Color(0xFF308C30)
private val expenseColors = Color(0xFFAF2222)

/**
 * Displays a finance item with the given information as list item.
 *
 * @param finance the finance item to display
 * @param onClicked the action to perform when the item is clicked
 * @param modifier the modifier to apply to the item
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 */
@Composable
fun FinanceItem(
    finance: Finance,
    onClicked: (finance: Finance) -> Unit,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR",
) {
    val amountSign = when (finance.category) {
        is FinanceCategory.Income -> "+"
        is FinanceCategory.Expense -> "-"
    }

    val amountColor = when (finance.category) {
        is FinanceCategory.Income -> incomeColors
        is FinanceCategory.Expense -> expenseColors
    }

    ListItem(
        headlineContent = {
            Text(
                text = finance.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        supportingContent = {
            Text(
                text = finance.description ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
            )
        },
        trailingContent = {
            Text(
                text = "$amountSign ${formatCurrency(value = finance.amount, code = currencyCode)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            )
        },
        modifier = modifier.combinedClickable(
            onClick = { onClicked(finance) },
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FinanceItemPreview() {
    MaterialTheme {
        FinanceItem(
            finance = Finance(
                title = "Salary",
                description = "Monthly salary",
                amount = 1000.0,
                interval = FinanceInterval.Monthly,
                category = FinanceCategory.Income(IncomeCategory.Salary)
            ),
            onClicked = {}
        )
    }
}