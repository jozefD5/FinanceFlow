package com.jd.financeflow.features.core.data.localDataSource

import kotlinx.coroutines.CancellationException

import android.util.Log

import com.jd.financeflow.features.core.data.localDataSource.database.FinanceDao
import com.jd.financeflow.features.core.data.localDataSource.mapers.toEntity
import com.jd.financeflow.features.core.data.localDataSource.mapers.toFinance
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.domain.models.finance.Finance

private const val TAG = "LocalFinanceDataSourceDefault"

/**
 * Default implementation of [LocalFinanceDataSource] that interacts with the Room database
 * via [FinanceDao].
 *
 * @property financeDao The data access object used for database operations.
 */
class LocalFinanceDataSourceDefault(
    private val financeDao: FinanceDao
) : LocalFinanceDataSource {

    /**
     * Retrieves all finance items from the local database.
     *
     * @return A [Result] containing a list of [Finance] objects on success, or a [DataError.Local] on failure.
     */
    override suspend fun getAllItems(): Result<List<Finance>, DataError.Local> {
        return try {
            Result.Success(
                financeDao.getAll().map { it.toFinance() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get finance items from local database, error: ${e.message}")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Retrieves a finance item by its unique identifier.
     *
     * @param id The unique identifier of the finance item.
     * @return A [Result] containing the [Finance] object on success, or a [DataError.Local] on failure.
     */
    override suspend fun getItemById(id: Int): Result<Finance, DataError.Local> {
        return try {
            val item =
                financeDao.getById(id) ?: return Result.Error(DataError.Local.INVALID_ARGUMENT)
            Result.Success(item.toFinance())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get finance item from local database by ID, error: ${e.message}")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Inserts a new finance item into the local database.
     *
     * @param item The [Finance] object to insert.
     * @return An [EmptyResult] indicating success, or a [DataError.Local] on failure.
     */
    override suspend fun insertItem(item: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.insert(item.toEntity())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "Failed to insert finance item to the local database, error: ${e.message}")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Updates an existing finance item in the local database.
     *
     * @param item The [Finance] object to update.
     * @return An [EmptyResult] indicating success, or a [DataError.Local] on failure.
     */
    override suspend fun updateItem(item: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.update(item.toEntity())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "Failed to update finance item in local database, error: ${e.message}")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Deletes a finance item from the local database.
     *
     * @param item The [Finance] object to delete.
     * @return An [EmptyResult] indicating success, or a [DataError.Local] on failure.
     */
    override suspend fun deleteItem(item: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.delete(item.toEntity())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "Failed to remove finance item from local database, error: ${e.message}")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}