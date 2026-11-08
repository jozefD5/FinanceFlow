package com.jd.financeflow.features.finance.presentation.components

import java.time.YearMonth
import java.time.ZoneId

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceInterval


/**
 * Calculate monthly cash flow for a given list of finances for a specific month and year.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
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
    val nextMonthStartMs =
        yearMonth.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

    for (item in items) {
        if (item.timeStampMs >= nextMonthStartMs) continue

        val amount = item.amount
        val itemDate = item.zonedDateTime
        val itemDay = itemDate.dayOfMonth
        val itemMonth = itemDate.monthValue

        val isStartedBeforeMonth = item.timeStampMs < monthStartMs

        when (item.interval) {
            FinanceInterval.OneTime -> {
                if (item.timeStampMs >= monthStartMs) {
                    result[itemDay - 1] += amount
                }
            }

            FinanceInterval.Daily -> {
                val startIdx = if (isStartedBeforeMonth) 0 else itemDay - 1
                for (i in startIdx until daysInMonth) {
                    result[i] += amount
                }
            }

            FinanceInterval.Weekly -> {
                val targetDayOfWeek = itemDate.dayOfWeek
                val firstDayOfMonth = yearMonth.atDay(1)
                val firstOccurrenceOffset =
                    (targetDayOfWeek.value - firstDayOfMonth.dayOfWeek.value + 7) % 7

                var dayIdx = firstOccurrenceOffset
                while (dayIdx < daysInMonth) {
                    val dayOfMonth = dayIdx + 1
                    if (isStartedBeforeMonth || dayOfMonth >= itemDay) {
                        result[dayIdx] += amount
                    }
                    dayIdx += 7
                }
            }

            FinanceInterval.Monthly -> {
                val dayToApply = minOf(itemDay, daysInMonth)
                result[dayToApply - 1] += amount
            }

            FinanceInterval.Quarterly -> {
                if (Math.floorMod(month - itemMonth, 3) == 0) {
                    val dayToApply = minOf(itemDay, daysInMonth)
                    result[dayToApply - 1] += amount
                }
            }

            FinanceInterval.SemiAnnually -> {
                if (Math.floorMod(month - itemMonth, 6) == 0) {
                    val dayToApply = minOf(itemDay, daysInMonth)
                    result[dayToApply - 1] += amount
                }
            }

            FinanceInterval.Annually -> {
                if (month == itemMonth || Math.floorMod(month - itemMonth, 12) == 0) {
                    val dayToApply = minOf(itemDay, daysInMonth)
                    result[dayToApply - 1] += amount
                }
            }
        }
    }
    return result.toList()
}

/**
 * Calculate total amount by category for a specific month and year.
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param items list of finances
 * @param year the year
 * @param month the month (1-12)
 * @return map of category order to total amount
 */
fun monthlyTotalByCategoryRange(
    items: List<Finance>,
    year: Int,
    month: Int,
): Map<Int, Double> {
    val zoneId = ZoneId.systemDefault()
    val totals = mutableMapOf<Int, Double>()

    for (item in items) {
        val contribution = calculateContributionForMonth(item, year, month, zoneId)
        if (contribution != 0.0) {
            val order = item.category.order
            totals[order] = (totals[order] ?: 0.0) + contribution
        }
    }
    return totals
}

/**
 * Calculate the annual cash flow for a given list of finances for a specific year.
 * Returns a list of 12 values, one for each month.
 *
 * This function runs calculations for each month in parallel using [Dispatchers.Default].
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param items list of finances
 * @param year the year
 * @return list of monthly totals (size 12)
 */
suspend fun annualCashFlow(
    items: List<Finance>,
    year: Int
): List<Double> = withContext(Dispatchers.Default) {
    val zoneId = ZoneId.systemDefault()
    (1..12).map { month ->
        async {
            items.sumOf { item ->
                calculateContributionForMonth(item, year, month, zoneId)
            }
        }
    }.awaitAll()
}

/**
 * Calculate the annual total by category for a given list of finances for a specific year.
 *
 * This function runs calculations for each month in parallel using [Dispatchers.Default].
 *
 * Note, method is expecting data to be grouped by finance type before use, income or expense.
 *
 * @param items list of finances
 * @param year the year
 * @return map of category order to total amount
 */
suspend fun annualTotalByCategory(
    items: List<Finance>,
    year: Int
): Map<Int, Double> = withContext(Dispatchers.Default) {
    val totals = mutableMapOf<Int, Double>()

    // Get monthly category totals for each month
    val monthlyCategoryTotal = (1..12).map { month ->
        // Get monthly totals for this month and year
        async {
            monthlyTotalByCategoryRange(items = items, year = year, month = month)
        }
    }.awaitAll()

    // Map categories to annual context
    monthlyCategoryTotal.forEach { categoryTotals ->
        categoryTotals.forEach { (category, amount) ->
            totals[category] = (totals[category] ?: 0.0) + amount
        }
    }

    return@withContext totals
}

/**
 * Helper to calculate the contribution of a finance item to a specific month.
 */
private fun calculateContributionForMonth(
    item: Finance,
    year: Int,
    month: Int,
    zoneId: ZoneId
): Double {
    val yearMonth = YearMonth.of(year, month)
    val monthStartMs = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    val nextMonthStartMs =
        yearMonth.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

    if (item.timeStampMs >= nextMonthStartMs) return 0.0

    val itemDate = item.zonedDateTime
    val amount = item.amount
    val itemMonth = itemDate.monthValue
    val itemDay = itemDate.dayOfMonth
    val isStartedBeforeMonth = item.timeStampMs < monthStartMs

    return when (item.interval) {
        FinanceInterval.OneTime -> if (!isStartedBeforeMonth) amount else 0.0
        FinanceInterval.Daily -> {
            val daysInMonth = yearMonth.lengthOfMonth()
            val startDay = if (isStartedBeforeMonth) 1 else itemDay
            (daysInMonth - startDay + 1) * amount
        }

        FinanceInterval.Weekly -> {
            val targetDayOfWeek = itemDate.dayOfWeek
            val firstDayOfMonth = yearMonth.atDay(1)
            val firstOccurrenceOffset =
                (targetDayOfWeek.value - firstDayOfMonth.dayOfWeek.value + 7) % 7
            val daysInMonth = yearMonth.lengthOfMonth()

            var count = 0
            var dayIdx = firstOccurrenceOffset
            while (dayIdx < daysInMonth) {
                if (isStartedBeforeMonth || (dayIdx + 1) >= itemDay) {
                    count++
                }
                dayIdx += 7
            }
            count * amount
        }

        FinanceInterval.Monthly -> amount
        FinanceInterval.Quarterly -> if (Math.floorMod(month - itemMonth, 3) == 0) amount else 0.0
        FinanceInterval.SemiAnnually -> if (Math.floorMod(month - itemMonth, 6) == 0) amount else 0.0
        FinanceInterval.Annually -> if (Math.floorMod(month - itemMonth, 12) == 0) amount else 0.0
    }
}