package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency

/**
 * Displays a balance card with the given value and currency code.
 *
 * @param balance the balance value to display
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @param modifier the modifier to apply to the card
 * @param color the background color of the card
 * @param textColor the text color of the card
 */
@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    currencyCode: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = stringResource(R.string.balance_card_label_your_balance),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = textColor,
                ),
                modifier = Modifier.alpha(0.8f)
            )
            Text(
                text = formatCurrency(balance, currencyCode),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.balance_card_label_income),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = textColor,
                        ),
                        modifier = Modifier.alpha(0.8f)
                    )
                    Text(
                        text = formatCurrency(income, currencyCode),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .heightIn(max = 32.dp)
                )

                Column {
                    Text(
                        text = stringResource(R.string.balance_card_label_expense),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = textColor,
                        ),
                        modifier = Modifier.alpha(0.8f)
                    )
                    Text(
                        text = formatCurrency(expense, currencyCode),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BalanceCardPreview() {
    MaterialTheme {
        Scaffold(
            containerColor = Color.Gray
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                BalanceCard(
                    balance = 100_000.0,
                    income = 150_000.0,
                    expense = 50_000.0,
                    currencyCode = "EUR",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}