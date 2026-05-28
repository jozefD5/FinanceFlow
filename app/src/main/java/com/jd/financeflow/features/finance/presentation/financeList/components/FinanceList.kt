package com.jd.financeflow.features.finance.presentation.financeList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

/**
 * Displays a list of finances in a lazy column.
 *
 * @param finances the list of finances to display
 * @param onItemClick the action to perform when an item is clicked
 * @param onItemLongClick the action to perform when an item is long clicked
 * @param modifier the modifier to apply to the list
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 */
@Composable
fun FinanceList(
    finances: List<Finance>,
    onItemClick: (Finance) -> Unit,
    onItemLongClick: (Finance) -> Unit,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR",
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(
            items = finances,
            key = { _, item -> item.id }
        ) { index, item ->
            FinanceListItem(
                finance = item,
                onClicked = onItemClick,
                onLongClicked = onItemLongClick,
                currencyCode = currencyCode,
            )

            if (index < (finances.size - 1)) {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f)
                )
            }
        }
    }
}

@Preview
@Composable
fun FinanceListPreview() {
    val currentUnixTime = System.currentTimeMillis()

    val finances = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            startDate = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            startDate = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus)
        ),
        Finance(
            title = "Investment",
            description = "",
            amount = 1000.0,
            startDate = currentUnixTime,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Investment)
        ),
        Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            startDate = currentUnixTime,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing)
        )
    )

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Scaffold { innerPadding ->
            FinanceList(
                finances = finances,
                onItemClick = {},
                onItemLongClick = {},
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}