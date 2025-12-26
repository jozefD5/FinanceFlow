package com.jd.financeflow.di

import com.jd.financeflow.features.finance.data.localDataSource.FinanceFactory
import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

import com.jd.financeflow.features.finance.data.repository.FinanceRepositoryImpl
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.presentation.transactionDetails.TransactionDetailsViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication

/**
 * Koin module for the FinanceFlow application.
 * This module provides dependencies for the FinanceFlow application.
 */
val appModule = module {
    single {
        FinanceFactory(androidApplication())
    }

    singleOf(::FinanceRepositoryImpl).bind<FinanceRepository>()

    viewModel { HomeViewModel(get()) }
    viewModel { (id: Int?) ->
        TransactionDetailsViewModel(
            get(),
            id
        )
    }
}