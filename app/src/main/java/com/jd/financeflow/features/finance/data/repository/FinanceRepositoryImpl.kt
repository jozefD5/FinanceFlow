package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
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
}