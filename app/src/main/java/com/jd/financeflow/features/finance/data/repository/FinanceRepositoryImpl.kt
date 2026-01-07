package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.finance.data.localDataSource.FinanceDao
import com.jd.financeflow.features.finance.data.mappers.toEntity
import com.jd.financeflow.features.finance.data.mappers.toFinance
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.FinanceType

/**
 * Finance repository implementation, allows to access and manage finance transactions.
 *
 * @property financeDao Finance data access object.
 */
class FinanceRepositoryImpl(
    private val financeDao: FinanceDao
) : FinanceRepository {

    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        return try {
            val data = financeDao.getById(id)

            if (data != null) {
                Result.Success(data.toFinance())
            } else {
                Result.Error(DataError.Local.INVALID_ARGUMENT)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getFinances(): Result<List<Finance>, DataError.Local> {
        return try {
            Result.Success(
                financeDao
                    .getAll().map { it.toFinance() }
                    .sortedByDescending { it.createdAt }
            )
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getTotal(
        type: FinanceType?,
        category: FinanceCategory?
    ): Result<Double, DataError.Local> {
        try {
            val finances = financeDao.getAll()
            val filteredFinances = finances.filter { item ->
                val typeMatch = if (type == null) {
                    true
                } else {
                    when (item.category) {
                        is FinanceCategory.Income -> type == FinanceType.INCOME
                        is FinanceCategory.Expense -> type == FinanceType.EXPENSE
                    }
                }

                val categoryMatch = category == null || item.category == category
                typeMatch && categoryMatch
            }

            val totalYear = filteredFinances.filter {
                it.interval == FinanceInterval.Yearly
            }.sumOf { it.amount }

            val totalMonth = filteredFinances.filter {
                it.interval == FinanceInterval.HalfYearly
            }.sumOf { it.amount } * 2

            val totalQuarterly = filteredFinances.filter {
                it.interval == FinanceInterval.Quarterly
            }.sumOf { it.amount } * 4

            val totalMonthly = filteredFinances.filter {
                it.interval == FinanceInterval.Monthly
            }.sumOf { it.amount } * 12

            val totalWeekly = filteredFinances.filter {
                it.interval == FinanceInterval.Weekly
            }.sumOf { it.amount } * 52

            val totalDaily = filteredFinances.filter {
                it.interval == FinanceInterval.Daily
            }.sumOf { it.amount } * 365

            val totalOnce = filteredFinances.filter {
                it.interval == FinanceInterval.Once
            }.sumOf { it.amount }

            val total =
                totalYear + totalMonth + totalQuarterly + totalMonthly + totalWeekly + totalDaily + totalOnce
            return Result.Success(total)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.update(finance.toEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.delete(finance.toEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            financeDao.insert(finance.toEntity())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}