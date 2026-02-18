package com.jd.financeflow.features.finance.presentation.financeList.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.features.core.presentation.components.CircleInitialAvatar
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import java.lang.System

/**
 * Displays a finance item with the given information as list item.
 *
 * @param finance the finance item to display
 * @param onClicked the action to perform when the item is clicked
 * @param onLongClicked the action to perform when the item is long clicked
 * @param modifier the modifier to apply to the item
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinanceListItem(
    finance: Finance,
    onClicked: (finance: Finance) -> Unit,
    onLongClicked: (finance: Finance) -> Unit,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR",
) {
    val amountSign = when (finance.category) {
        is FinanceCategory.Income -> "+"
        is FinanceCategory.Expense -> "-"
    }

    val amountColor = when (finance.category) {
        is FinanceCategory.Income -> MaterialTheme.colorScheme.primary
        is FinanceCategory.Expense -> MaterialTheme.colorScheme.error
    }

    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = finance.title,
                style = MaterialTheme.typography.titleMediumEmphasized
            )
        },
        supportingContent = {
            Text(
                text = finance.description ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            CircleInitialAvatar(name = finance.title)
        },
        trailingContent = {
            Text(
                text = "$amountSign ${formatCurrency(value = finance.amount, code = currencyCode)}",
                style = MaterialTheme.typography.titleMediumEmphasized.copy(
                    color = amountColor
                )
            )
        },
        modifier = modifier.combinedClickable(
            onClick = { onClicked(finance) },
            onLongClick = { onLongClicked(finance) }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FinanceListItemPreview() {
    MaterialTheme {
        FinanceListItem(
            finance = Finance(
                title = "Salary",
                description = "Monthly salary",
                amount = 1000.0,
                interval = FinanceInterval.Monthly,
                startDate = System.currentTimeMillis(),
                category = FinanceCategory.Income(IncomeCategory.Salary)
            ),
            onClicked = {},
            onLongClicked = {}
        )
    }
}