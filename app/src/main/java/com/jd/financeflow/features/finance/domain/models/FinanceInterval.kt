package com.jd.financeflow.features.finance.domain.models

/**
 * Represents the different intervals for financial transactions.
 */
enum class FinanceInterval(val value: Int) {
    OneTime(0),
    Daily(1),
    Weekly(2),
    Monthly(3),
    Quarterly(4),
    SemiAnnually(5),
    Annually(6)
}
