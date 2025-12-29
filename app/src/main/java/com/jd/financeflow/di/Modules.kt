package com.jd.financeflow.di

import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import org.koin.android.ext.koin.androidApplication

import com.jd.financeflow.features.finance.data.localDataSource.FinanceDatabase
import com.jd.financeflow.features.finance.data.localDataSource.FinanceFactory
import com.jd.financeflow.features.finance.data.repository.FinanceRepositoryImpl
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.presentation.financeDetails.FinanceDetailsViewModel
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

    singleOf(::FinanceRepositoryImpl).bind<FinanceRepository>()

    viewModel { HomeViewModel(get()) }
    viewModel { (id: Int?) ->
        FinanceDetailsViewModel(
            get(),
            id
        )
    }
}