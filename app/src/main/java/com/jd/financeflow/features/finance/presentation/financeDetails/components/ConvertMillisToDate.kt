package com.jd.financeflow.features.finance.presentation.financeDetails.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Convert millis to date string.
 *
 * @param millis millis to convert
 * @return date string in format MM/dd/yyyy
 */
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}