package com.jd.financeflow.features.finance.data.localDataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for storing finance transactions.
 */
@Database(
    entities = [FinanceEntity::class],
    version = 1,
)
@TypeConverters(
    IntFinanceIntervalConverter::class,
    StringFinanceCategoryConverter::class
)
abstract class FinanceDatabase : RoomDatabase() {

    /**
     * Provides access to finance transaction Data Access Object.
     *
     * @return FinanceDao
     */
    abstract fun financeDao(): FinanceDao

    /**
     * Companion object to provide access to the database name.
     */
    companion object {
        const val DATABASE_NAME = "finance_db"
    }
}