package com.jd.financeflow.features.finance.presentation.components

import android.util.Log
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval

private const val daysInMonth = 31
private const val monthsInYear = 12


fun monthlyCashFlow(
    items: List<Finance>,
    startTimeMs: Long,
    endTimeMs: Long,
): List<Double> {
    val dailyAverages = MutableList(daysInMonth) { 0.0 }

    Log.d("monthlyCashFlow", "startTimeMs: $startTimeMs")
    Log.d("monthlyCashFlow", "endTimeMs: $endTimeMs")


    // Process repeated finance items for the specific month. This includes any finance items
    // from previous months with valid intervals (repeated up to monthly bases).
    items
        .filter { item ->
            item.timeStampMs in endTimeMs..startTimeMs
        }
        .forEach { item ->
            val dayIndex = item.zonedDateTime.dayOfMonth - 1
            when (item.interval) {
                FinanceInterval.Daily -> {
                    for (i in 0 until daysInMonth) dailyAverages[i] += item.amount
                }

                FinanceInterval.Weekly -> {
                    for (i in 0..4) {
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

                else -> {}
            }
        }

    // Process one-time finance items for the specific month.
    items
        .filter { item ->
            item.timeStampMs in endTimeMs..startTimeMs
        }
        .forEach { item ->
            val dayIndex = item.zonedDateTime.dayOfMonth - 1

            if (item.interval == FinanceInterval.OneTime ||
                item.interval >= FinanceInterval.Quarterly
            ) {
                dailyAverages[dayIndex] += item.amount
            }
        }

    return dailyAverages
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
    val dailyAverages = MutableList(daysInMonth) { 0.0 }

    Log.d("avgMonthlyCashFlow", "monthIndex: $monthIndex")

    // Process repeated finance items for the specific month. This includes any finance items
    // from previous months with valid intervals (repeated up to monthly bases).
    financeItems
        .filter { item ->
            item.zonedDateTime.monthValue <= monthIndex
        }
        .forEach { item ->
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

                else -> {}
            }
        }

    // Process one-time finance items for the specific month.
    financeItems
        .filter { item ->
            item.zonedDateTime.monthValue == monthIndex
        }
        .forEach { item ->
            val dayIndex = item.zonedDateTime.dayOfMonth - 1

            if (item.interval == FinanceInterval.OneTime ||
                item.interval >= FinanceInterval.Quarterly
            ) {
                dailyAverages[dayIndex] += item.amount
            }
        }

    return dailyAverages
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
