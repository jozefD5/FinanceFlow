package com.jd.financeflow.features.finance.domain

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceType

/**
 * Finance transaction repository interface, defining the methods for interacting with
 * financial transactions.
 */
interface FinanceRepository {
    /**
     * Retrieves a list of all financial transactions.
     *
     * @return List of [com.jd.financeflow.features.core.domain.finance.Finance] objects on success or [DataError.Local] on failure.
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
     * Retrieves a list of financial transactions based on their type.
     *
     * @param type The type of the transactions to retrieve.
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinancesByType(type: FinanceType): Result<List<Finance>, DataError.Local>

    /**
     * Retrieves a list of financial transactions based on their category.
     *
     * @param category The category of the transactions to retrieve.
     * @return List of [Finance] objects on success or [DataError.Local] on failure.
     */
    suspend fun getFinancesByCategory(category: FinanceCategory): Result<List<Finance>, DataError.Local>

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
     * @param id The unique identifier of the transaction to delete.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun deleteFinance(id: Int): EmptyResult<DataError.Local>

    /**
     * Inserts a new financial transaction.
     *
     * @param finance The [Finance] object to insert.
     * @return [EmptyResult] on success or [DataError.Local] on failure.
     */
    suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local>

    /**
     * Retrieves the total income for a specific type of financial transactions.
     *
     * @param type The type of the transactions to retrieve the total income for.
     * @param category The category of the transactions to retrieve the total income for. If not
     * provided, the total income for all categories will be calculated.
     * @return The total income as a [Double] value.
     */
    suspend fun getTotal(type: FinanceType, category: FinanceCategory? = null): Double
}