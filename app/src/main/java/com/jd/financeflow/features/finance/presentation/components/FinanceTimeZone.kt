package com.jd.financeflow.features.finance.presentation.components

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceInterval

/**
 * Get zoned date time from finance item
 *
 * @return zoned date time
 */
val Finance.zonedDateTime: ZonedDateTime
    get() = Instant.ofEpochMilli(timeStampMs).atZone(ZoneId.systemDefault())


/**
 * Get end time duration in milliseconds from start time duration in milliseconds.
 *
 * @param interval interval of finance item
 *
 */
fun Long.endTimeDuration(interval: FinanceInterval): Long {
    val startInstant = Instant.ofEpochMilli(this)
    val startDateTime = startInstant.atZone(ZoneId.systemDefault())

    val endDateTime = when (interval) {
        FinanceInterval.OneTime -> startDateTime
        FinanceInterval.Daily -> startDateTime.plusDays(1)
        FinanceInterval.Weekly -> startDateTime.plusWeeks(1)
        FinanceInterval.Monthly -> startDateTime.plusMonths(1)
        FinanceInterval.Quarterly -> startDateTime.plusMonths(3)
        FinanceInterval.SemiAnnually -> startDateTime.plusMonths(6)
        FinanceInterval.Annually -> startDateTime.plusYears(1)
    }
    return endDateTime.toInstant().toEpochMilli()
}




