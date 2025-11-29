package com.jd.financeflow.features.core.presentation.components

import android.icu.text.NumberFormat
import android.icu.util.Currency

/**
 * Formats a double value to a currency string. If the formatting fails, the original string
 * is returned.
 *
 * @param value the double value to format
 * @param code the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @return the formatted currency string
 */
fun formatCurrency(
    value: Double,
    code: String,
    precision: Int = 2
): String {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(code)
        formatter.maximumFractionDigits = precision

        return formatter.format(value)
    } catch (_: Exception) {
        value.toString()
    }
}