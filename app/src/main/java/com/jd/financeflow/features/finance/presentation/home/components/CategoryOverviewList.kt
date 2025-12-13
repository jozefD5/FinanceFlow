package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.core.domain.finance.FinanceCategory

@Composable
fun CategoryOverviewList(
    categories: List<Pair<FinanceCategory, Double>>,
    currencyCode: String,
    onClicked: (FinanceCategory) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
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
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryOverviewListPreview() {
    val categories = listOf(
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Salary),
            100_000.0
        ),
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Bonus),
            50_000.0
        ),
        Pair(
            FinanceCategory.Income(FinanceCategory.IncomeCategory.Investments),
            40_000.0
        )
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            CategoryOverviewList(
                categories = categories,
                currencyCode = "EUR",
                onClicked = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
