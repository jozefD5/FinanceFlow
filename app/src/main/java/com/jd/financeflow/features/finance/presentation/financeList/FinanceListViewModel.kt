package com.jd.financeflow.features.finance.presentation.financeList

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jd.financeflow.features.core.domain.DataError
import com.jd.financeflow.features.core.domain.Result
import com.jd.financeflow.features.core.presentation.toUiText
import com.jd.financeflow.features.finance.domain.FinanceRepository
import com.jd.financeflow.features.finance.domain.models.FilterConfiguration
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * View model for the FinanceList screen.
 */
class FinanceListViewModel(
    private val repository: FinanceRepository,
    private val currencyCode: String = "EUR",
    private val filterConfiguration: FilterConfiguration? = null
) : ViewModel() {
    private val _state = MutableStateFlow(
        FinanceListState(
            currencyCode = currencyCode,
            filterConfiguration = filterConfiguration
        )
    )
    val state = _state
        .onStart {
            applyFilterConfiguration(filterConfiguration)
            refreshFinanceList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinanceListState()
        )

    /**
     * Handles actions for the FinanceList screen.
     *
     * @param action action to be handled
     */
    fun onAction(action: FinanceListActions) {
        when (action) {
            is FinanceListActions.OnClearError -> {
                _state.update { it.copy(error = null) }
            }

            is FinanceListActions.OnAddFinanceClicked -> {}
            is FinanceListActions.OnFilterClicked -> {}
            is FinanceListActions.OnFinanceItemClicked -> {}
            is FinanceListActions.OnFinanceItemDelete -> {
                deleteFinance(action.finance)
            }

            is FinanceListActions.OnSelectedDeleteFinanceItem -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = action.shoeDialog,
                        selectedDeleteFinance = action.finance
                    )
                }
            }

            is FinanceListActions.OnUpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = action.value) }
            }

            is FinanceListActions.OnSearch -> {
                refreshFinanceList()
            }

            is FinanceListActions.OnUpdateFilterConfiguration -> {}

        }
    }

    /**
     * Apply filter configuration.
     *
     * @param config new filter configuration
     */
    private fun applyFilterConfiguration(config: FilterConfiguration?) = viewModelScope.launch {

        // Get maximum value of finances (+10%) or set to null if list is empty
        val highestAmount = runCatching {
            when (val result = repository.getFinances()) {
                is Result.Success -> {
                    val value = if (result.data.isEmpty()) {
                        null
                    } else {
                        result.data.maxOfOrNull { it.amount }
                    }
                    value?.times(1.1)
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
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
            return@launch
        }

        // If provided, use supplied configuration with updated max amount limit. Else, Create new
        // filtered configuration object.
        when (config) {
            is FilterConfiguration -> {
                config.copy(
                    miniAmountLimit = 0.0,
                    maxAmountLimit = highestAmount
                )
            }

            null -> {
                FilterConfiguration(
                    miniAmountLimit = 0.0,
                    maxAmountLimit = highestAmount
                )
            }
        }

        // Update state with new configuration
        _state.update {
            it.copy(
                currencyCode = currencyCode,
                filterConfiguration = config,
            )
        }


        Log.d("FinanceListViewModel", "Filter configuration: $config")
    }

    /**
     * Refreshes the list of finances.
     */
    private fun refreshFinanceList() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            val items = when (val result = repository.getFinances()) {
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

            _state.update {
                it.copy(
                    isLoading = false,
                    finances = items
                )
            }

        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText()
                )
            }
        }
    }

    /**
     * Deletes a finance.
     *
     * @param finance finance to be deleted
     */
    private fun deleteFinance(finance: Finance) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        runCatching {
            when (val result = repository.deleteFinance(finance)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            selectedDeleteFinance = null,
                            showDeleteDialog = false
                        )
                    }
                    refreshFinanceList()
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText(),
                            selectedDeleteFinance = null,
                            showDeleteDialog = false
                        )
                    }
                }
            }
        }.getOrElse {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = DataError.Local.UNKNOWN.toUiText(),
                    selectedDeleteFinance = null,
                    showDeleteDialog = false
                )
            }
        }
    }
}