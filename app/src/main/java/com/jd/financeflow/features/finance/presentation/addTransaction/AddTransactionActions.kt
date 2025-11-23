package com.jd.financeflow.features.finance.presentation.addTransaction

sealed interface AddTransactionActions {
    object SaveTransaction : AddTransactionActions
    object ClearError : AddTransactionActions
    data class UpdateTitle(val title: String) : AddTransactionActions
    data class UpdateDescription(val description: String) : AddTransactionActions
    data class UpdateAmount(val amount: String) : AddTransactionActions
    data class UpdateFinanceInterval(val financeIntervalIndex: Int) : AddTransactionActions
    data class UpdateFinanceType(val financeTypeIndex: Int) : AddTransactionActions
    data class UpdateFinanceCategory(val financeCategoryIndex: Int) : AddTransactionActions
}