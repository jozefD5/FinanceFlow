package com.jd.financeflow.features.finance.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.components.formatCurrency

/**
 * Hero balance card showing total balance and income/expense chips.
 *
 * @param balance the net balance to display
 * @param income total income value
 * @param expense total expense value
 * @param currencyCode ISO currency code, e.g. "EUR"
 * @param color card container color (use Transparent when rendered on a gradient)
 * @param textColor text and icon tint color
 */
@Composable
fun BalanceCard(
    balance: Double,
    income: Double,
    expense: Double,
    currencyCode: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.balance_card_label_your_balance),
                style = MaterialTheme.typography.labelMedium.copy(color = textColor),
                modifier = Modifier.alpha(0.70f),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = formatCurrency(balance, currencyCode),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                ),
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                FinanceSummaryChip(
                    label = stringResource(R.string.balance_card_label_income),
                    amount = formatCurrency(income, currencyCode),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconTint = Color(0xFF66BB6A),
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(10.dp))
                FinanceSummaryChip(
                    label = stringResource(R.string.balance_card_label_expense),
                    amount = formatCurrency(expense, currencyCode),
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    iconTint = Color(0xFFEF5350),
                    textColor = textColor,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FinanceSummaryChip(
    label: String,
    amount: String,
    icon: ImageVector,
    iconTint: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .background(iconTint.copy(alpha = 0.20f), RoundedCornerShape(10.dp)),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(color = textColor),
                    modifier = Modifier.alpha(0.70f),
                )
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun BalanceCardPreview() {
    MaterialTheme {
        Surface(
            color = Color(0xFF0D172A),
            modifier = Modifier.fillMaxWidth(),
        ) {
            BalanceCard(
                balance = 100_000.0,
                income = 150_000.0,
                expense = 50_000.0,
                currencyCode = "EUR",
                color = Color.Transparent,
                textColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
        }
    }
}
