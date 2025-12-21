package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.TransactionType

class FinanceRepositoryImpl() : FinanceRepository {
    private val finances = mutableListOf<Finance>()

    override suspend fun getFinances(): Result<List<Finance>, DataError.Local> {
        return try {
            Result.Success(finances)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            val index = finances.indexOfFirst { it.id == finance.id }
            if (index != -1) {
                finances[index] = finance
                Result.Success(Unit)
            } else {
                Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteFinance(id: Int): EmptyResult<DataError.Local> {
        return try {
            finances.removeIf { it.id == id }
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        return try {
            finances.add(finance)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getTotal(
        type: TransactionType?,
        category: FinanceCategory?
    ): Result<Double, DataError.Local> {
        try {
            val filteredFinances = finances.filter { item ->
                val typeMatch = if (type == null) {
                    true
                } else {
                    when (item.category) {
                        is FinanceCategory.Income -> type == TransactionType.INCOME
                        is FinanceCategory.Expense -> type == TransactionType.EXPENSE
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
}