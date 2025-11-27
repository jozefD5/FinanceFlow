package com.jd.financeflow.features.finance.presentation.home

import androidx.lifecycle.ViewModel
import com.jd.financeflow.features.finance.domain.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View model for the home screen.
 *
 * @param repository repository for finance transactions
 */
class HomeViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

}