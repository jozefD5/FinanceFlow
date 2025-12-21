package com.jd.financeflow.features.finance.data.localDataSource

import androidx.room.TypeConverter
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

///**
// * Converts between [FinanceInterval] and its integer representation.
// */
//object IntFinanceIntervalConverter {
//    @TypeConverter
//    fun fromFinanceInterval(value: FinanceInterval): Int {
//        return value.value
//    }
//
//    @TypeConverter
//    fun toFinanceInterval(value: Int): FinanceInterval {
//        return FinanceInterval.entries.first { it.value == value }
//    }
//}
//
///**
// * Converts between [FinanceType] and its integer representation.
// */
//object IntFinanceTypeConverter {
//    @TypeConverter
//    fun fromFinanceType(value: FinanceType): Int {
//        return value.value
//    }
//
//    @TypeConverter
//    fun toFinanceType(value: Int): FinanceType {
//        return FinanceType.entries.first {
//            it.value == value
//        }
//    }
//}
//
//object StringFinanceCategoryConverter {
//    @TypeConverter
//    fun fromFinanceCategory(value: FinanceCategory): String {
//        return when (value) {
//            is FinanceCategory.Income -> {
//                buildJsonObject {
//                    put("type", "1")
//                    put("category", "2")
//                }.toString()
//            }
//
//            is FinanceCategory.Expense -> {
//                buildJsonObject {
//                    put("type", "0")
//                    put("category", "2")
//                }.toString()
//            }
//        }
//    }
//}
//
