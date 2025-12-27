package com.jd.financeflow.features.finance.data.mappers

import com.jd.financeflow.features.finance.data.localDataSource.FinanceEntity
import com.jd.financeflow.features.finance.domain.models.Finance

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
        interval = this.interval,
        category = this.category,
        createdAt = this.createdAt,
    )
}