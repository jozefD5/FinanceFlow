package com.jd.financeflow.features.core.domain.localDataSource

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.EmptyResult
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.domain.finance.Finance

interface LocalFinanceDataSource {
    suspend fun getAllItems(): Result<List<Finance>, DataError.Local>

    suspend fun getItemById(id: Int): Result<Finance, DataError.Local>

    suspend fun insertItem(item: Finance): EmptyResult<DataError.Local>

    suspend fun updateItem(item: Finance): EmptyResult<DataError.Local>

    suspend fun deleteItem(item: Finance): EmptyResult<DataError.Local>
}