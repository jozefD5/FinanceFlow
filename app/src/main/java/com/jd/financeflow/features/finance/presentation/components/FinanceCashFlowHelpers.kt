package com.jd.financeflow.features.finance.presentation.components

import java.time.Instant
import java.time.ZoneId

import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval

/**
 * Get zoned date time from finance item
 */
private val Finance.zonedDateTime
    get() = Instant.ofEpochMilli(timeStampMs).atZone(ZoneId.systemDefault())

/**
 * Calculate average monthly values for each day of the month using data for the full year.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param financeItems finances used for calculation
 */
fun avgMonthlyCashFlow(financeItems: List<Finance>): List<Double> {
    val daysInMonth = 31
    val dailyAverages = MutableList(daysInMonth) { 0.0 }

    financeItems.forEach { item ->
        val dayIndex = item.zonedDateTime.dayOfMonth - 1

        when (item.interval) {
            FinanceInterval.Daily -> {
                for (i in 0 until daysInMonth) dailyAverages[i] += item.amount
            }

            FinanceInterval.Weekly -> {
                // Apply to the specific day and every 7 days forward/backward in the month
                for (i in -4..4) {
                    val actualIndex = dayIndex + (i * 7)
                    if (actualIndex in 0 until daysInMonth) {
                        dailyAverages[actualIndex] += item.amount
                    }
                }
            }

            FinanceInterval.Monthly -> {
                if (dayIndex in 0 until daysInMonth) {
                    dailyAverages[dayIndex] += item.amount
                }
            }

            else -> 0.0
        }
    }

    return dailyAverages
}

/**
 * Annual cash flow with respect to date and interval of individual finance item.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 * @param financeItems finance items used for processing
 */
fun annualCashFlow(financeItems: List<Finance>): List<Double> {
    val monthsInYear = 12
    val monthlyTotal = MutableList(monthsInYear) { 0.0 }

    financeItems.forEach { item ->
        val date = item.zonedDateTime
        val startMonth = date.monthValue - 1

        when (item.interval) {
            // These apply to EVERY month starting from the first record
            FinanceInterval.Daily -> {
                val monthlyAmount = item.amount * 30.44
                for (i in startMonth until monthsInYear) monthlyTotal[i] += monthlyAmount
            }

            FinanceInterval.Weekly -> {
                val monthlyAmount = item.amount * 4.34
                for (i in startMonth until monthsInYear) monthlyTotal[i] += monthlyAmount
            }

            FinanceInterval.Monthly -> {
                for (i in startMonth until monthsInYear) monthlyTotal[i] += item.amount
            }

            // Multi-month spikes
            FinanceInterval.Quarterly -> {
                for (i in 0 until monthsInYear step 3) {
                    val idx = startMonth + i
                    if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                }
            }

            FinanceInterval.HalfYearly -> {
                for (i in 0 until monthsInYear step 6) {
                    val idx = startMonth + i
                    if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                }
            }

            FinanceInterval.Yearly -> {
                monthlyTotal[startMonth] += item.amount
            }
        }
    }
    return monthlyTotal
}