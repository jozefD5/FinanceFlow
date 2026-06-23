package com.jd.financeflow.features.finance.presentation.charts.monthlyChart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jd.financeflow.features.finance.domain.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class MonthlyChartViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(
        MonthlyChartState(
            currencyCode = currencyCode,
            selectedMonthIndex = LocalDate.now().monthValue
        )
    )

    val state = _state
        .onStart { }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: MonthlyChartActions) {
        when (action) {
            is MonthlyChartActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is MonthlyChartActions.OnRefresh -> {}
            is MonthlyChartActions.OnToggleChartOption -> {}
            is MonthlyChartActions.OnSelectYear -> {}
            is MonthlyChartActions.OnSelectMonth -> {}
        }
    }

}