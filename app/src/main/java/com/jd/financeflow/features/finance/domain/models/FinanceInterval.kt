package com.jd.financeflow.features.finance.domain.models

/**
 * Represents the different intervals for financial transactions.
 */
enum class FinanceInterval(val value: Int) {
    Once(0),
    Daily(1),
    Weekly(2),
    Monthly(3),
    Quarterly(4),
    HalfYearly(5),
    Yearly(6)
}
