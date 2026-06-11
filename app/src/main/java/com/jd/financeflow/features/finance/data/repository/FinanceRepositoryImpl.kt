package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.onError
import com.jd.financeflow.features.core.domain.onSuccess
import com.jd.financeflow.features.finance.data.localStorageDataSource.LocalStorageDataSource
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.FinanceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override suspend fun getTotal(
        type: FinanceType?,
        category: FinanceCategory?
    ): Result<Double, DataError.Local> {
        return try {
            val finances = when (val result = localDataSource.getAll()) {
                is Result.Success -> result.data
                is Result.Error -> return Result.Error(result.error)
            }

            val total = finances.sumOf { item ->
                // Check for Type and Category matches
                val typeMatch = type == null || when (item.category) {
                    is FinanceCategory.Income -> type == FinanceType.INCOME
                    is FinanceCategory.Expense -> type == FinanceType.EXPENSE
                }
                val categoryMatch = category == null || item.category == category

                if (typeMatch && categoryMatch) {
                    // Scale amount to a Yearly Total based on interval
                    when (item.interval) {
                        FinanceInterval.HalfYearly -> item.amount * 2
                        FinanceInterval.Quarterly -> item.amount * 4
                        FinanceInterval.Monthly -> item.amount * 12
                        FinanceInterval.Weekly -> item.amount * 52
                        FinanceInterval.Daily -> item.amount * 365
                        FinanceInterval.Yearly -> item.amount
                    }
                } else {
                    0.0
                }
            }

            Result.Success(total)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun totalMonthly(category: FinanceCategory): Result<Double, DataError.Local> {
        return withContext(Dispatchers.IO) {
            // Get data
            val finances = when (val result = localDataSource.getAll()) {
                is Result.Success -> result.data.filter { it.category == category }
                is Result.Error -> return@withContext Result.Error(result.error)
            }

            val groupedFinances = finances.groupBy { it.interval }

            // Calculate
            try {
                val total = groupedFinances.entries.sumOf { (interval, items) ->
                    val sum = items.sumOf { it.amount }
                    when (interval) {
                        FinanceInterval.Daily -> (sum * 365.0) / 12.0
                        FinanceInterval.Weekly -> sum * 4.34524
                        FinanceInterval.Monthly -> sum
                        FinanceInterval.Quarterly -> sum / 3.0
                        FinanceInterval.HalfYearly -> sum / 6.0
                        FinanceInterval.Yearly -> sum / 12.0
                    }
                }
                Result.Success(total)
            } catch (_: Exception) {
                Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
            }
        }
    }
}