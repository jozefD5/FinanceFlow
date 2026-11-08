package com.jd.financeflow.features.financeDetails.data

import com.jd.financeflow.features.core.domain.finance.Finance
import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.financeDetails.domain.FinanceDetailsRepository
import kotlinx.coroutines.CancellationException

class FinanceDetailsRepositoryDefault(
    private val localDataSource: LocalFinanceDataSource
) : FinanceDetailsRepository {
    override suspend fun getFinanceById(id: Int): Result<Finance, DataError.Local> {
        try {

        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {

        }
    }

    override suspend fun insertFinance(finance: Finance): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun updateFinance(finance: Finance): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }
}