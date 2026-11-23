package com.jd.financeflow.features.core.domain.localDataSource

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.models.finance.Finance

/**
 * Interface representing the local data source for financial transactions.
 */
interface LocalFinanceDataSource {
    /**
     * Retrieves all financial items.
     *
     * @return A [Result] containing a list of [Finance] objects on success, or [DataError.Local] on failure.
     */
    suspend fun getAllItems(): Result<List<Finance>, DataError.Local>

    /**
     * Retrieves a financial item by its unique identifier.
     *
     * @param id The unique identifier of the item.
     * @return A [Result] containing the [Finance] object on success, or [DataError.Local] on failure.
     */
    suspend fun getItemById(id: Int): Result<Finance, DataError.Local>

    /**
     * Inserts a new financial item.
     *
     * @param item The [Finance] object to insert.
     * @return An [EmptyResult] indicating success, or [DataError.Local] on failure.
     */
    suspend fun insertItem(item: Finance): EmptyResult<DataError.Local>

    /**
     * Updates an existing financial item.
     *
     * @param item The [Finance] object to update.
     * @return An [EmptyResult] indicating success, or [DataError.Local] on failure.
     */
    suspend fun updateItem(item: Finance): EmptyResult<DataError.Local>

    /**
     * Deletes a financial item.
     *
     * @param item The [Finance] object to delete.
     * @return An [EmptyResult] indicating success, or [DataError.Local] on failure.
     */
    suspend fun deleteItem(item: Finance): EmptyResult<DataError.Local>
}