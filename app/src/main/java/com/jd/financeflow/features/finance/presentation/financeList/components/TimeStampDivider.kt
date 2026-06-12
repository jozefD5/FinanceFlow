package com.jd.financeflow.features.finance.presentation.financeList.components

import java.time.Instant
import java.time.ZoneId

import kotlinx.datetime.Month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp


/**
 * Displays a timestamp in the finance list.
 *
 * @param timestamp the timestamp to display
 * @param modifier the modifier to apply to the timestamp
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimeStampDivider(
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
    Column(modifier = modifier) {
        Text(
            text = "${
                Month.entries[date.monthValue - 1].name.lowercase(LocalLocale.current.platformLocale)
                    .replaceFirstChar { it.uppercaseChar() }
            } - ${date.year}",
            style = MaterialTheme.typography.titleMediumEmphasized,
        )
        HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())
    }
}