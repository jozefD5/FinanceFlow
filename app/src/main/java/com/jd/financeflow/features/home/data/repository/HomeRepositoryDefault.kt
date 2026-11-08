package com.jd.financeflow.features.home.data.repository

import kotlinx.coroutines.CancellationException

import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.home.domain.HomeRepository


class HomeRepositoryDefault(
    val localDataSource: LocalFinanceDataSource
) : HomeRepository {
    override suspend fun getFinances(): Result<List<Finance>, DataError.Local> {
        return try {
            when (val result = localDataSource.getAllItems()) {
                is Result.Success -> Result.Success(result.data.sortedByDescending { it.createdAt })
                is Result.Error -> Result.Error(result.error)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}