package com.jd.financeflow.features.core.presentation.components

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency

/**
 * Formats a double value to a currency string, ensuring there is a non-breaking space between the currency symbol and the number.
 * If the formatting fails, the original string is returned.
 *
 * @param value the double value to format
 * @param code the currency code to use e.g. EUR for Euro or USD for US Dollar
 * @param precision the number of fraction digits to use
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
        formatter.minimumFractionDigits = precision

        if (formatter is DecimalFormat) {
            val nonBreakingSpace = "\u00A0"

            // Add a non-breaking space to prefixes/suffixes if they contain text but don't already have a space.
            if (formatter.positivePrefix.isNotEmpty() && !formatter.positivePrefix.endsWith(nonBreakingSpace)) {
                formatter.positivePrefix = formatter.positivePrefix + nonBreakingSpace
            }
            if (formatter.positiveSuffix.isNotEmpty() && !formatter.positiveSuffix.startsWith(nonBreakingSpace)) {
                formatter.positiveSuffix = nonBreakingSpace + formatter.positiveSuffix
            }
            if (formatter.negativePrefix.isNotEmpty() && !formatter.negativePrefix.endsWith(nonBreakingSpace)) {
                formatter.negativePrefix = formatter.negativePrefix + nonBreakingSpace
            }
            if (formatter.negativeSuffix.isNotEmpty() && !formatter.negativeSuffix.startsWith(nonBreakingSpace)) {
                formatter.negativeSuffix = nonBreakingSpace + formatter.negativeSuffix
            }
        }

        return formatter.format(value)
    } catch (_: Exception) {
        value.toString()
    }
}
