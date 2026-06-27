package com.jd.financeflow.features.finance.presentation.components

import java.time.YearMonth
import java.time.ZoneId

import android.util.Log

import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval


private const val monthsInYear = 12

/**
 * Calculate monthly cash flow for a given list of finances for a specific month and year.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 * @param items list of finances, e.g. income or expense
 * @param year the year
 * @param month the month (1-12)
 * @return list of daily cash flow values for the month
 */
fun monthlyCashFlow(
    items: List<Finance>,
    year: Int,
    month: Int,
): List<Double> {
    val yearMonth = YearMonth.of(year, month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val result = DoubleArray(daysInMonth)

    val zoneId = ZoneId.systemDefault()
    val monthStartMs = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    val monthEndMs =
        yearMonth.atEndOfMonth().atTime(23, 59, 59, 999000000).atZone(zoneId).toInstant()
            .toEpochMilli()

    for (item in items) {
        if (item.timeStampMs > monthEndMs) continue

        val itemDate = item.zonedDateTime
        val amount = item.amount
        val itemDayIndex = itemDate.dayOfMonth - 1
        val itemMonth = itemDate.monthValue

        when (item.interval) {
            FinanceInterval.OneTime -> {
                if (item.timeStampMs in monthStartMs..monthEndMs && itemDayIndex in 0 until daysInMonth) {
                    result[itemDayIndex] += amount
                }
            }

            FinanceInterval.Daily -> {
                val start = if (item.timeStampMs < monthStartMs) 0 else itemDayIndex
                if (start < daysInMonth) {
                    for (i in start until daysInMonth) result[i] += amount
                }
            }

            FinanceInterval.Weekly -> {
                val targetDayOfWeek = itemDate.dayOfWeek
                val firstDayOfMonth = yearMonth.atDay(1)
                val firstOccurrenceOffset =
                    (targetDayOfWeek.value - firstDayOfMonth.dayOfWeek.value + 7) % 7

                var dayIdx = firstOccurrenceOffset
                while (dayIdx < daysInMonth) {
                    if (item.timeStampMs < monthStartMs) {
                        result[dayIdx] += amount
                    } else {
                        val occurrenceMs =
                            yearMonth.atDay(dayIdx + 1).atStartOfDay(zoneId).toInstant()
                                .toEpochMilli()
                        if (occurrenceMs >= item.timeStampMs) {
                            result[dayIdx] += amount
                        }
                    }
                    dayIdx += 7
                }
            }

            FinanceInterval.Monthly -> {
                if (itemDayIndex in 0 until daysInMonth) {
                    result[itemDayIndex] += amount
                }
            }

            FinanceInterval.Quarterly -> {
                if ((month - itemMonth) % 3 == 0 && itemDayIndex in 0 until daysInMonth) {
                    result[itemDayIndex] += amount
                }
            }

            FinanceInterval.SemiAnnually -> {
                if ((month - itemMonth) % 6 == 0 && itemDayIndex in 0 until daysInMonth) {
                    result[itemDayIndex] += amount
                }
            }

            FinanceInterval.Annually -> {
                if (month == itemMonth && itemDayIndex in 0 until daysInMonth) {
                    result[itemDayIndex] += amount
                }
            }
        }
    }
    return result.toList()
}


/**
 * Calculate total amount by category for a specific month and year.
 *
 * @param items list of finances
 * @param year the year
 * @param month the month (1-12)
 * @return map of category order to total amount
 */
fun totalByCategoryRange(
    items: List<Finance>,
    year: Int,
    month: Int,
): Map<Int, Double> {
    return items.groupBy { it.category.order }
        .mapValues { (_, categoryItems) ->
            monthlyCashFlow(categoryItems, year, month).sum()
        }
}


//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------


/**
 * Calculate average monthly values for each day of the month using data for the full year.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param financeItems finances used for calculation
 * @param monthIndex index of the month for which to calculate the average
 * @return list of average monthly values
 */
fun avgMonthlyCashFlow(
    financeItems: List<Finance>,
    monthIndex: Int
): List<Double> {
    val dailyAverages = DoubleArray(31)

    Log.d("avgMonthlyCashFlow", "monthIndex: $monthIndex")

    for (item in financeItems) {
        val itemDate = item.zonedDateTime
        val itemMonth = itemDate.monthValue
        val dayIndex = itemDate.dayOfMonth - 1
        if (dayIndex !in 0..30) continue

        val amount = item.amount

        // Recurring items (Daily, Weekly, Monthly) that started before or in the target month
        if (itemMonth <= monthIndex) {
            when (item.interval) {
                FinanceInterval.Daily -> {
                    for (i in 0..30) dailyAverages[i] += amount
                }

                FinanceInterval.Weekly -> {
                    // Apply to the specific day and every 7 days forward/backward in the month
                    for (i in -4..4) {
                        val idx = dayIndex + (i * 7)
                        if (idx in 0..30) dailyAverages[idx] += amount
                    }
                }

                FinanceInterval.Monthly -> {
                    dailyAverages[dayIndex] += amount
                }

                else -> {}
            }
        }

        // One-time and long-term recurring items (only for the specific month)
        if (itemMonth == monthIndex) {
            if (item.interval == FinanceInterval.OneTime || item.interval >= FinanceInterval.Quarterly) {
                dailyAverages[dayIndex] += amount
            }
        }
    }

    return dailyAverages.toList()
}

/**
 * Annual cash flow with respect to date and interval of individual finance item.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 * @param financeItems finance items used for processing
 * @return list of annual cash flow values
 */
fun annualCashFlow(financeItems: List<Finance>): List<Double> {
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

            FinanceInterval.Quarterly -> {
                for (i in 0 until monthsInYear step 3) {
                    val idx = startMonth + i
                    if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                }
            }

            FinanceInterval.SemiAnnually -> {
                for (i in 0 until monthsInYear step 6) {
                    val idx = startMonth + i
                    if (idx < monthsInYear) monthlyTotal[idx] += item.amount
                }
            }

            FinanceInterval.Annually -> {
                monthlyTotal[startMonth] += item.amount
            }

            else -> {
                // TODO: Handle other intervals
            }
        }
    }
    return monthlyTotal
}

/**
 * Calculates the total annual amount for a single finance item based on its interval and start date.
 *
 * @param item the finance item
 * @return total annual amount
 */
fun calculateAnnualAmount(item: Finance): Double {
    val startMonth = item.zonedDateTime.monthValue - 1
    val monthsRemaining = monthsInYear - startMonth

    return when (item.interval) {
        FinanceInterval.Daily -> item.amount * 30.44 * monthsRemaining
        FinanceInterval.Weekly -> item.amount * 4.34 * monthsRemaining
        FinanceInterval.Monthly -> item.amount * monthsRemaining
        FinanceInterval.Quarterly -> {
            (0 until monthsInYear step 3).count { startMonth + it < monthsInYear } * item.amount
        }

        FinanceInterval.SemiAnnually -> {
            (0 until monthsInYear step 6).count { startMonth + it < monthsInYear } * item.amount
        }

        FinanceInterval.Annually -> item.amount

        else -> 0.0 // TODO: Handle other intervals
    }
}

/**
 * Annual total by category, grouped by finance type. Get total financial amount by category and interval.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param financeItems finance items used for processing
 * @return list of pairs containing category order and total amount
 */
fun annualTotalByCategory(financeItems: List<Finance>): Map<Int, Double> {
    return financeItems.groupBy { it.category.order }
        .mapValues { (_, items) -> items.sumOf { calculateAnnualAmount(it) } }
}
