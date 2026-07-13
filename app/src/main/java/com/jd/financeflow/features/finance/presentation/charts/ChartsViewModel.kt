package com.jd.financeflow.features.finance.presentation.charts

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.ViewModel


/**
 * Finance charts view model
 *
 * @param totalPages Total number of pages to display
 * @param currencyCode Currency code, e.g. "EUR" for Euro
 */
class ChartsViewModel(
    val totalPages: Int = 120,
    private val currencyCode: String = "EUR",
) : ViewModel() {
    // Calculate the initial page based on the current month
    val initialPage: Int = totalPages / 2

    // Force the base date to always be the 1st of the current month
    val baseData: LocalDate = LocalDate.now().withDayOfMonth(1)
    val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val yearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")

    // Main UI state
    private val _state = MutableStateFlow(
        ChartsState(currencyCode = currencyCode)
    )
    val state = _state.asStateFlow()

    /**
     * On action handler
     * @param action Action to be handled
     */
    fun onAction(action: ChartsActions) {
        when (action) {
            is ChartsActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is ChartsActions.OnTabSelected -> {
                _state.update { it.copy(selectedIntervalIndex = action.index) }
            }
        }
    }
}
