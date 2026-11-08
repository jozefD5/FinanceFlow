package com.jd.financeflow.features.financeDetails.domain

import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.DataError.Local
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result

interface FinanceDetailsRepository {
    /**
     * Retrieves a specific financial transaction by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return The [Finance] object with the specified ID on success or [DataError.Local] on failure.
     */
    suspend fun getFinanceById(id: Int): Result<Finance, Local>

    /**
     * Inserts a new financial transaction.
     *
     * @param finance The [Finance] object to insert.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local>

    /**
     * Updates an existing financial transaction.
     *
     * @param finance The updated [Finance] object.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local>
}