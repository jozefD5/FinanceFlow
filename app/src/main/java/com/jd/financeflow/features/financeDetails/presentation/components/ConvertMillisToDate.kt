package com.jd.financeflow.features.financeDetails.presentation.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Converts epoch milliseconds to a date string using the device's default locale.
 *
 * @param millis epoch milliseconds to convert
 * @return date string in format dd/MM/yyyy
 */
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}