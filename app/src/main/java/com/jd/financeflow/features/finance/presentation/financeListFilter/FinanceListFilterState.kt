package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.FinanceCategory

data class FinanceListFilterState(
    val error: UiText? = null,

    val selectedTypeIndex: Int = 0,
    val selectedCategories: List<FinanceCategory> = emptyList(),

    val enableAmountRange: Boolean = false,
    val selectedAmountRangeMini: Double? = null,
    val selectedAmountRangeMax: Double? = null
)
