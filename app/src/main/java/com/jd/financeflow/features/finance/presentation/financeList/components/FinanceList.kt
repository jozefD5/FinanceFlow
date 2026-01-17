package com.jd.financeflow.features.finance.presentation.financeList.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.R
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
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(finances, key = { it.id }) { item ->
            FinanceListItem(
                finance = item,
                onClicked = onItemClick,
                onLongClicked = onItemLongClick,
                currencyCode = currencyCode,
            )
        }
    }
}

@Preview
@Composable
fun FinanceListPreview() {
    val finances = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus)
        ),
        Finance(
            title = "Investment",
            description = "",
            amount = 1000.0,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Investments)
        ),
        Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
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