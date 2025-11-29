package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.features.core.presentation.components.formatCurrency

/**
 * Displays a balance card with the given value and currency code.
 *
 * @param value the balance value to display
 * @param currencyCode the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @param modifier the modifier to apply to the card
 * @param color the background color of the card
 * @param textColor the text color of the card
 */
@Composable
fun BalanceCard(
    value: Double,
    currencyCode: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    ElevatedCard(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = "Your balance",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = textColor,
                ),
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = formatCurrency(value, currencyCode),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
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
            BalanceCard(
                value = 100.0, currencyCode = "EUR", modifier = Modifier.padding(innerPadding)
            )
        }
    }
}