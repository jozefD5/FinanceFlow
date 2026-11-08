package com.jd.financeflow.features.home.domain

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.DataError.Local
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.finance.Finance

/**
 * Repository interface for managing home screen data and financial transactions.
 */
interface HomeRepository {
    /**
     * Retrieves a list of all financial transactions.
     *
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinances(): Result<List<Finance>, Local>
}