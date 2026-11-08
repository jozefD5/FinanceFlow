package com.jd.financeflow.features.core.data.localDataSource.database

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

import androidx.room.TypeConverter

import com.jd.financeflow.features.core.domain.finance.ExpenseCategory
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.domain.finance.FinanceInterval
import com.jd.financeflow.features.core.domain.finance.FinanceType
import com.jd.financeflow.features.core.domain.finance.IncomeCategory

/**
 * Converts between [FinanceInterval] and its integer representation.
 */
object IntFinanceIntervalConverter {
    @TypeConverter
    fun fromFinanceInterval(value: FinanceInterval): Int {
        return value.value
    }

    @TypeConverter
    fun toFinanceInterval(value: Int): FinanceInterval {
        return FinanceInterval.entries.first { it.value == value }
    }
}

/**
 * Converts between [FinanceType] and its JSON (string) representation.
 */
object StringFinanceCategoryConverter {
    @TypeConverter
    fun fromFinanceCategory(value: FinanceCategory): String {
        return when (value) {
            is FinanceCategory.Income -> {
                buildJsonObject {
                    put("type", FinanceType.INCOME.ordinal)
                    put("category", value.category.ordinal)
                }.toString()
            }

            is FinanceCategory.Expense -> {
                buildJsonObject {
                    put("type", FinanceType.EXPENSE.ordinal)
                    put("category", value.category.ordinal)
                }.toString()
            }
        }
    }

    @TypeConverter
    fun toFinanceCategory(value: String): FinanceCategory {
        val json = Json.parseToJsonElement(value)
        val typeIndex = json.jsonObject["type"]?.jsonPrimitive?.content?.toInt()
        val categoryIndex = json.jsonObject["category"]?.jsonPrimitive?.content?.toInt()

        if (typeIndex == null || categoryIndex == null) {
            throw IllegalArgumentException("Invalid FinanceCategory JSON: $value")
        }

        return when (typeIndex) {
            FinanceType.INCOME.ordinal -> {
                FinanceCategory.Income(IncomeCategory.entries[categoryIndex])
            }

            FinanceType.EXPENSE.ordinal -> {
                FinanceCategory.Expense(ExpenseCategory.entries[categoryIndex])
            }

            else -> {
                throw IllegalArgumentException("Invalid FinanceType index: $typeIndex")
            }
        }
    }
}

