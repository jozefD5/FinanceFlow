package com.jd.financeflow.features.core.data.localDataSource.mapers

import com.jd.financeflow.features.core.data.localDataSource.database.FinanceEntity
import com.jd.financeflow.features.core.domain.models.finance.Finance

/**
 * Convert Finance to FinanceEntity
 *
 * @return FinanceEntity
 */
fun Finance.toEntity(): FinanceEntity {
    return FinanceEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        amount = this.amount,
        startDate = this.timeStampMs,
        endDataDate = null,
        interval = this.interval,
        category = this.category,
        createdAt = this.createdAt,
    )
}

/**
 * Convert FinanceEntity to Finance
 *
 * @return Finance
 */
fun FinanceEntity.toFinance(): Finance {
    return Finance(
        id = this.id,
        title = this.title,
        description = this.description,
        amount = this.amount,
        timeStampMs = this.startDate,
        interval = this.interval,
        category = this.category,
        createdAt = this.createdAt,
    )
}