package com.jd.financeflow.di

import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import org.koin.android.ext.koin.androidApplication

import com.jd.financeflow.features.finance.data.database.FinanceDatabase
import com.jd.financeflow.features.finance.data.database.FinanceFactory
import com.jd.financeflow.features.finance.data.localStorageDataSource.LocalStorageDataSource
import com.jd.financeflow.features.finance.data.repository.FinanceRepositoryImpl
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsViewModel
import com.jd.financeflow.features.finance.presentation.financeList.FinanceListViewModel
import com.jd.financeflow.features.finance.presentation.financeListFilter.FinanceListFilterViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeViewModel

/**
 * Koin module for the FinanceFlow application.
 * This module provides dependencies for the FinanceFlow application.
 */
val appModule = module {
    //Creates a [FinanceDatabase] instance using the provided [FinanceFactory].
    single {
        FinanceFactory(androidApplication()).create().build()
    }
    // Provides a [FinanceDao] instance using the provided [FinanceDatabase].
    single {
        get<FinanceDatabase>().financeDao()
    }

    single { LocalStorageDataSource(financeDao = get()) }
    singleOf(::FinanceRepositoryImpl).bind<FinanceRepository>()

    viewModel { HomeViewModel(get()) }
    viewModel { (financeId: Int?) ->
        FinanceDetailsViewModel(
            financeRepository = get(),
            financeId = financeId
        )
    }
    viewModel { FinanceListViewModel(get()) }

    viewModel { (currencyCode: String, configuration: FilterConfiguration?) ->
        FinanceListFilterViewModel(
            currencyCode = currencyCode,
            configuration = configuration
        )
    }
}