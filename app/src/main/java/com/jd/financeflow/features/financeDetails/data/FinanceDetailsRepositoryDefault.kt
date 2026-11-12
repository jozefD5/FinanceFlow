package com.jd.financeflow.features.financeDetails.data

import kotlinx.coroutines.CancellationException

import com.jd.financeflow.features.core.domain.models.finance.Finance
import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.financeDetails.domain.FinanceDetailsRepository

/**
 * Default implementation of [FinanceDetailsRepository] backed by a local data source.
 *
 * Any unexpected exception is mapped to [DataError.Local.UNKNOWN], while [CancellationException]
 * is rethrown to preserve coroutine cancellation.
 *
 * @property localDataSource The local data source used to access finance items.
 */
class FinanceDetailsRepositoryDefault(
    private val localDataSource: LocalFinanceDataSource
) : FinanceDetailsRepository {
    /**
     * Retrieves a specific financial transaction by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return A [Result] containing the [Finance] on success, or [DataError.Local] on failure.
     */
    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        return try {
            localDataSource.getItemById(id)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Inserts a new financial transaction into the local data source.
     *
     * @param finance The [Finance] object to insert.
     * @return [EmptyResult] on success, or [DataError.Local] on failure.
     */
    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            localDataSource.insertItem(finance)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Updates an existing financial transaction in the local data source.
     *
     * @param finance The updated [Finance] object.
     * @return [EmptyResult] on success, or [DataError.Local] on failure.
     */
    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            localDataSource.updateItem(finance)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}