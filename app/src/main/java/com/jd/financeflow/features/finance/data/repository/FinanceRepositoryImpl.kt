package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.core.domain.finance.FinanceType

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

    override suspend fun getFinancesByType(type: FinanceType): Result<List<Finance>, DataError.Local> {
        return try {
            val filteredFinances = finances.filter { it.type == type }
            Result.Success(filteredFinances)
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun getFinancesByCategory(category: FinanceCategory): Result<List<Finance>, DataError.Local> {
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
        type: FinanceType,
        category: FinanceCategory?,
    ): Double {
        val filteredFinances = finances.filter {
            it.type == type && (category == null || it.category == category)
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

        return totalYear + totalMonth + totalQuarterly + totalMonthly + totalWeekly + totalDaily + totalOnce
    }
}