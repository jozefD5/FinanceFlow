package com.jd.financeflow.features.finance.presentation.charts.financeChartCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.financeflow.features.finance.domain.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class FinanceChartCardViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceChartCardState(currencyCode = currencyCode))

    val state = _state
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: FinanceChartCardActions) {
        when (action) {
            is FinanceChartCardActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceChartCardActions.OnRefresh -> {}
        }
    }

}