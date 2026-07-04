package com.jd.financeflow.features.finance.presentation.components.financeList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.core.presentation.components.CircleInitialAvatar
import com.jd.financeflow.features.core.presentation.components.formatCurrency
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

/**
 * Displays a finance item with the given information as list item.
 *
 * @param finance the finance item to display
 * @param modifier the modifier to apply to the item
 * @param onClicked the action to perform when the item is clicked
 * @param onLongClicked the action to perform when the item is long clicked
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun FinanceListItem(
    finance: Finance,
    modifier: Modifier = Modifier,
    onClicked: ((finance: Finance) -> Unit)? = null,
    onLongClicked: ((finance: Finance) -> Unit)? = null,
    currencyCode: String = "EUR",
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                enabled = !(onClicked == null && onLongClicked == null),
                onClick = { onClicked?.invoke(finance) },
                onLongClick = { onLongClicked?.invoke(finance) }
            )
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = finance.title,
                    style = MaterialTheme.typography.titleMediumEmphasized
                )
            },
            supportingContent = finance.description?.takeIf { it.isNotBlank() }
                ?.let { description ->
                    {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
            leadingContent = {
                CircleInitialAvatar(name = finance.title)
            },
            trailingContent = {
                Text(
                    text = "${finance.category.amountSign} ${
                        formatCurrency(
                            value = finance.amount,
                            code = currencyCode
                        )
                    }",
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(
                        textAlign = TextAlign.End,
                        color = finance.category.amountColor
                    ),
                    maxLines = 1
                )
            }
        )
    }
}

private val FinanceCategory.amountSign: String
    get() = when (this) {
        is FinanceCategory.Income -> "+"
        is FinanceCategory.Expense -> "-"
    }

private val FinanceCategory.amountColor: Color
    @Composable
    get() = when (this) {
        is FinanceCategory.Income -> MaterialTheme.colorScheme.primary
        is FinanceCategory.Expense -> MaterialTheme.colorScheme.error
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
                timeStampMs = System.currentTimeMillis(),
                category = FinanceCategory.Income(IncomeCategory.Salary)
            ),
            onClicked = {},
            onLongClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceListItemNoDescriptionPreview() {
    MaterialTheme {
        FinanceListItem(
            finance = Finance(
                title = "Coffee",
                description = null,
                amount = 5.50,
                interval = FinanceInterval.Monthly,
                timeStampMs = System.currentTimeMillis(),
                category = FinanceCategory.Expense(com.jd.financeflow.features.finance.domain.models.ExpenseCategory.Entertainment)
            ),
            onClicked = {},
            onLongClicked = {}
        )
    }
}
