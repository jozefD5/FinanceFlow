package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.finance.data.localStorageDataSource.LocalStorageDataSource
import com.jd.financeflow.features.core.domain.models.finance.Finance
import com.jd.financeflow.features.finance.domain.FinanceRepository

/**
 * Finance repository implementation, allows to access and manage finance transactions.
 *
 * @property localDataSource The data source for local storage operations.
 */
class FinanceRepositoryImpl(
    private val localDataSource: LocalStorageDataSource
) : FinanceRepository {
    override suspend fun getFinances(): Result<List<Finance>, DataError.Local> {
        return try {
            when (val result = localDataSource.getAll()) {
                is Result.Success -> Result.Success(
                    result.data.sortedByDescending { it.createdAt }
                )

                is Result.Error -> Result.Error(result.error)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        return try {
            when (val result = localDataSource.getById(id)) {
                is Result.Success -> Result.Success(result.data)
                is Result.Error -> Result.Error(result.error)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            when (val result = localDataSource.insert(finance)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.error)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            when (val result = localDataSource.update(finance)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.error)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            when (val result = localDataSource.delete(finance)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Error(result.error)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}