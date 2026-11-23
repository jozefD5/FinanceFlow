package com.jd.financeflow.features.charts.domain

import com.jd.financeflow.features.core.domain.models.finance.Finance
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.DataError.Local
import com.jd.financeflow.features.core.domain.result.Result

/**
 * Repository interface for managing finance items acquisition for visual presentation
 */
interface ChartsRepository {
    /**
     * Retrieves a list of all financial transactions.
     *
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinances(): Result<List<Finance>, Local>
}