package com.jd.financeflow.di

import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.core.module.dsl.viewModel

import com.jd.financeflow.features.core.data.localDataSource.database.FinanceDatabase
import com.jd.financeflow.features.core.data.localDataSource.database.FinanceFactory
import com.jd.financeflow.features.core.domain.localDataSource.LocalFinanceDataSource
import com.jd.financeflow.features.core.data.localDataSource.LocalFinanceDataSourceDefault

import com.jd.financeflow.features.home.data.repository.HomeRepositoryDefault
import com.jd.financeflow.features.home.domain.HomeRepository
import com.jd.financeflow.features.home.presentation.HomeViewModel


/**
 * Koin module for the FinanceFlow application.
 * This module provides dependencies for the FinanceFlow application.
 */
val appModule = module {
    // -------------------------------------------------------------------------------
    // Finance local database
    // -------------------------------------------------------------------------------
    //Creates a [FinanceDatabase] instance using the provided [FinanceFactory].
    single { FinanceFactory(androidApplication()).create().build() }

    // Provides a [FinanceDao] instance using the provided [FinanceDatabase].
    single { get<FinanceDatabase>().financeDao() }

    singleOf(::LocalFinanceDataSourceDefault).bind<LocalFinanceDataSource>()
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // Home view
    // -------------------------------------------------------------------------------
    singleOf(::HomeRepositoryDefault).bind<HomeRepository>()
    viewModel { HomeViewModel(repository = get()) }
    // -------------------------------------------------------------------------------


//    single { LocalStorageDataSource(financeDao = get()) }
//    singleOf(::FinanceRepositoryImpl).bind<FinanceRepository>()

//    viewModel { HomeViewModel(get()) }
//
//    viewModel { (financeId: Int?, currencyCode: String) ->
//        FinanceDetailsViewModel(
//            financeRepository = get(),
//            financeId = financeId,
//            currencyCode = currencyCode
//        )
//    }
//
//    viewModel { (currencyCode: String, configuration: FilterConfiguration?) ->
//        FinanceListViewModel(
//            repository = get(),
//            currencyCode = currencyCode,
//            filterConfiguration = configuration
//        )
//    }
//
//    viewModel { (currencyCode: String, configuration: FilterConfiguration?) ->
//        FinanceListFilterViewModel(
//            currencyCode = currencyCode,
//            configuration = configuration
//        )
//    }
//
//    viewModel { (currencyCode: String) ->
//        ChartsViewModel(currencyCode = currencyCode)
//    }
//
//    viewModel { (startTimeMs: Long, interval: FinanceInterval, currencyCode: String) ->
//        FinanceChartCardViewModel(
//            repository = get(),
//            startTimeMs = startTimeMs,
//            interval = interval,
//            currencyCode = currencyCode
//        )
//    }
}