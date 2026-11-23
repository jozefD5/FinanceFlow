package com.jd.financeflow.features.core.data.localDataSource.database

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
     * Companion object to provide access to database constants.
     */
    companion object {
        /**
         * The file name of the Room database.
         */
        const val DATABASE_NAME = "finance_db"
    }
}