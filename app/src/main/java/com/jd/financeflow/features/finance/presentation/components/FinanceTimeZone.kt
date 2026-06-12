package com.jd.financeflow.features.finance.presentation.components

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Get zoned date time from finance item
 */
val Finance.zonedDateTime: ZonedDateTime
    get() = Instant.ofEpochMilli(timeStampMs).atZone(ZoneId.systemDefault())