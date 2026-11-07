package com.jd.financeflow.features.finance.domain

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Finance transaction repository interface, defining the methods for interacting with
 * financial transactions.
 */
interface FinanceRepository {
    /**
     * Retrieves a list of all financial transactions.
     *
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinances(): Result<List<Finance>, DataError.Local>

    /**
     * Retrieves a specific financial transaction by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return The [Finance] object with the specified ID on success or [DataError.Local] on failure.
     */
    suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local>

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

    /**
     * Deletes a financial transaction by its unique identifier.
     *
     * @param finance The [Finance] object to delete.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun deleteFinance(finance: Finance): EmptyResult<DataError.Local>
}