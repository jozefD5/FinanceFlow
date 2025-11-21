package com.jd.financeflow.features.finance.data.repository

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.EmptyResult
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.finance.domain.Finance
import com.jd.financeflow.features.finance.domain.FinanceCategory
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.FinanceType

class FinanceRepositoryImpl(

): FinanceRepository {
    override suspend fun getFinances(): Result<List<Finance>, DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun getFinancesByType(type: FinanceType): Result<List<Finance>, DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun getFinancesByCategory(category: FinanceCategory): Result<List<Finance>, DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFinance(id: Int): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }
}