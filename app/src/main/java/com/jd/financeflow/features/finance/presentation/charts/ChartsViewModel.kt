package com.jd.financeflow.features.finance.presentation.charts

import java.time.Instant
import java.time.ZoneId

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.domain.onError
import com.jd.financeflow.features.core.domain.onSuccess
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.Finance
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory
import com.jd.financeflow.features.finance.domain.models.chart.BarChartData
import com.jd.financeflow.features.finance.presentation.components.toUiText


class ChartsViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceChartsState(currencyCode = currencyCode))
    val state = _state
        .onStart {
            // refreshBarData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceChartsState(currencyCode = currencyCode)
        )

    /**
     * On action handler
     * @param action Action to be handled
     */
    fun onAction(action: FinanceChartsActions) {
        when (action) {
            is FinanceChartsActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private suspend fun calculateCacheFlow() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        val finances = runCatching {
            when (val result = repository.getFinances()) {
                is Result.Success -> {
                    result.data
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    }
                    return@launch
                }
            }

        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.STORAGE_UNAVAILABLE.toUiText()
                )
            }
            return@launch
        }

        val incomeAvgList = calculateAveragePerMonthDay(finances.filter {
            it.category.type == FinanceType.INCOME
        })

        val expenseAvgList = calculateAveragePerMonthDay(finances.filter {
            it.category.type == FinanceType.EXPENSE
        })


    }

    private suspend fun calculateAveragePerMonthDay(finances: List<Finance>): List<Double> {
        val buffer = mutableListOf<Double>()

        // Group items by day of month
        val groupedFinances = finances.groupBy { item ->
            Instant.ofEpochMilli(item.timeStampMs)
                .atZone(ZoneId.systemDefault())
                .dayOfMonth


        }

        // Calculate average financial amount for month while taking into account full year. This
        // allows to include intervals such as quarterly of half-yearly and use they average
        // amount for a day in year. So if something is £360 per year, its average is £1 per day.
        groupedFinances.entries.forEach { (_, items) ->
            val intervalGroupedFinances = items.groupBy { it.interval }

            // Calculate average day of month value (financial amount) using interval as base
            // for averaging as required
            val total = intervalGroupedFinances.entries.sumOf { (interval, subItems) ->
                val sum = subItems.sumOf { it.amount }
                when (interval) {
                    FinanceInterval.Once, FinanceInterval.Yearly -> sum / 365.0
                    FinanceInterval.Daily -> sum
                    FinanceInterval.Weekly -> (sum * 4.34524) / 365.0
                    FinanceInterval.Monthly -> (sum * 12.0) / 365.0
                    FinanceInterval.Quarterly -> (sum * 4) / 365.0
                    FinanceInterval.HalfYearly -> (sum * 2) / 365.0
                }
            }
            buffer.add(total)
        }

        return buffer
    }


}
