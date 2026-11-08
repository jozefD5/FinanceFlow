package com.jd.financeflow.features.home.data

import kotlinx.coroutines.CancellationException

import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.home.domain.HomeRepository


/**
 * Default implementation of [HomeRepository] that fetches financial transactions from a local data source.
 *
 * @property localDataSource The local data source used to access finance items.
 */
class HomeRepositoryDefault(
    private val localDataSource: LocalFinanceDataSource
) : HomeRepository {
    /**
     * Retrieves a list of all financial transactions sorted by creation date descending.
     *
     * @return A [Result] containing a list of [Finance] objects on success, or [DataError.Local] on failure.
     */
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