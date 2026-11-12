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

import com.jd.financeflow.features.core.domain.models.financeFilter.FilterConfiguration

import com.jd.financeflow.features.home.data.HomeRepositoryDefault
import com.jd.financeflow.features.home.domain.HomeRepository
import com.jd.financeflow.features.home.presentation.HomeViewModel

import com.jd.financeflow.features.financeDetails.data.FinanceDetailsRepositoryDefault
import com.jd.financeflow.features.financeDetails.domain.FinanceDetailsRepository
import com.jd.financeflow.features.financeDetails.presentation.FinanceDetailsViewModel

import com.jd.financeflow.features.financeList.data.FinanceListRepositoryDefault
import com.jd.financeflow.features.financeList.domain.FinanceListRepository
import com.jd.financeflow.features.financeList.presentation.financeList.FinanceListViewModel
import com.jd.financeflow.features.financeList.presentation.financeFilter.FinanceListFilterViewModel

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

    // -------------------------------------------------------------------------------
    // Finance details view
    // -------------------------------------------------------------------------------
    singleOf(::FinanceDetailsRepositoryDefault).bind<FinanceDetailsRepository>()
    viewModel { FinanceDetailsViewModel(repository = get()) }
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // Finance list view
    // -------------------------------------------------------------------------------
    singleOf(::FinanceListRepositoryDefault).bind<FinanceListRepository>()
    viewModel { (currencyCode: String, configuration: FilterConfiguration?) ->
        FinanceListViewModel(
            repository = get(),
            currencyCode = currencyCode,
            filterConfiguration = configuration
        )
    }

    viewModel { (currencyCode: String, configuration: FilterConfiguration?) ->
        FinanceListFilterViewModel(
            currencyCode = currencyCode,
            configuration = configuration
        )
    }
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