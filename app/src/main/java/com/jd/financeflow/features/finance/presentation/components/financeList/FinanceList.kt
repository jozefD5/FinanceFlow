package com.jd.financeflow.features.finance.presentation.components.financeList

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.YearMonth

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.presentation.components.zonedDateTime


/**
 * Displays a list of finances in a lazy column.
 *
 * @param finances the list of finances to display
 * @param onItemClick the action to perform when an item is clicked
 * @param onItemLongClick the action to perform when an item is long clicked
 * @param modifier the modifier to apply to the list
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceList(
    finances: List<Finance>,
    onItemClick: (Finance) -> Unit,
    onItemLongClick: (Finance) -> Unit,
    modifier: Modifier = Modifier,
    currencyCode: String = "EUR",
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // Group and sort finances by month and year.
    val groupedFinances = remember(finances) {
        finances
            .sortedByDescending { it.timeStampMs }
            .groupBy {
                val date = it.zonedDateTime
                YearMonth.of(date.year, date.month)
            }
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        contentPadding = contentPadding
    ) {
        groupedFinances.forEach { (yearMonth, financesInMonth) ->
            stickyHeader(key = yearMonth) {
                val locale = LocalLocale.current.platformLocale
                val headerText = remember(yearMonth, locale) {
                    val formatter = DateTimeFormatter.ofPattern("MMMM - yyyy", locale)
                    yearMonth.format(formatter).replaceFirstChar { it.uppercaseChar() }
                }

                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(
                items = financesInMonth,
                key = { it.id }
            ) { item ->
                FinanceListItem(
                    finance = item,
                    onClicked = onItemClick,
                    onLongClicked = onItemLongClick,
                    currencyCode = currencyCode,
                )
            }
        }
    }
}


@Preview
@Composable
fun FinanceListPreview() {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val dateString1 = "15/05/2025"
    val dateString2 = "10/03/2026"
    val dateString3 = "20/05/2026"

    val timestampMs1 = LocalDate
        .parse(dateString1, formatter)
        .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()

    val timestampMs2 = LocalDate
        .parse(dateString2, formatter)
        .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()

    val timestampMs3 = LocalDate
        .parse(dateString3, formatter)
        .atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()


    val finances = listOf(
        Finance(
            title = "Salary",
            description = "Monthly salary",
            amount = 10_000.0,
            timeStampMs = timestampMs1,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Salary 2",
            description = "Monthly salary",
            amount = 5_000.0,
            timeStampMs = timestampMs1,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Salary 3",
            description = "Monthly salary",
            amount = 3_000.0,
            timeStampMs = timestampMs1,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Income(IncomeCategory.Salary),
        ),
        Finance(
            title = "Bonus",
            description = "Work bonus",
            amount = 5_000.0,
            timeStampMs = timestampMs2,
            interval = FinanceInterval.Quarterly,
            category = FinanceCategory.Income(IncomeCategory.Bonus)
        ),
        Finance(
            title = "Housing",
            description = "Rent",
            amount = 500.0,
            timeStampMs = timestampMs3,
            interval = FinanceInterval.Monthly,
            category = FinanceCategory.Expense(ExpenseCategory.Housing)
        )
    )

    MaterialTheme {
        Scaffold { innerPadding ->
            FinanceList(
                finances = finances,
                onItemClick = {},
                onItemLongClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}