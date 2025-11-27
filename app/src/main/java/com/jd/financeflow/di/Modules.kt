package com.jd.financeflow.di

import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

import com.jd.financeflow.features.finance.data.repository.FinanceRepositoryImpl
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.presentation.editTransaction.EditTransactionViewModel
import com.jd.financeflow.features.finance.presentation.home.HomeViewModel

/**
 * Koin module for the FinanceFlow application.
 * This module provides dependencies for the FinanceFlow application.
 */
val appModule = module {
    singleOf(::FinanceRepositoryImpl).bind<FinanceRepository>()

    viewModel { (id: Int?) ->
        EditTransactionViewModel(
            financeRepository = get(),
            transactionId = id
        )
    }

    viewModel { HomeViewModel(get()) }
}