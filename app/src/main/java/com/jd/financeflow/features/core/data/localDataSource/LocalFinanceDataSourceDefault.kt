package com.jd.financeflow.features.core.data.localDataSource

import kotlinx.coroutines.CancellationException

import android.util.Log

import com.jd.financeflow.features.core.data.localDataSource.database.FinanceDao
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource

import com.jd.financeflow.features.finance.data.mappers.toEntity
import com.jd.financeflow.features.finance.data.mappers.toFinance
import com.jd.financeflow.features.core.domain.finance.Finance

private const val TAG = "LocalFinanceDataSourceDefault"

class LocalFinanceDataSourceDefault(
    private val financeDao: FinanceDao
) : LocalFinanceDataSource {

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