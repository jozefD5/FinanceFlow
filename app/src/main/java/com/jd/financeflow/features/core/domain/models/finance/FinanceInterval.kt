package com.jd.financeflow.features.core.domain.models.finance

/**
 * Represents the different intervals for financial transactions.
 *
 * @property value The integer representation of the interval.
 */
enum class FinanceInterval(val value: Int) {
    /** One-time transaction. */
    OneTime(0),
    /** Daily recurring transaction. */
    Daily(1),
    /** Weekly recurring transaction. */
    Weekly(2),
    /** Monthly recurring transaction. */
    Monthly(3),
    /** Quarterly recurring transaction. */
    Quarterly(4),
    /** Semi-annually recurring transaction. */
    SemiAnnually(5),
    /** Annually recurring transaction. */
    Annually(6)
}
