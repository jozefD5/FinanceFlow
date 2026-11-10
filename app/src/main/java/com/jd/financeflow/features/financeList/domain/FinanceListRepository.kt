package com.jd.financeflow.features.financeList.domain

import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result

interface FinanceListRepository {
    /**
     * Retrieves a list of all financial transactions.
     *
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinances(): Result<List<Finance>, DataError.Local>

    /**
     * Deletes a financial transaction by its unique identifier.
     *
     * @param finance The [Finance] object to delete.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun deleteFinance(finance: Finance): EmptyResult<DataError.Local>
}