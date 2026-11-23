package com.jd.financeflow.features.core.data.localDataSource.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Factory class for creating a Room database builder.
 * @param context The application context.
 */
class FinanceFactory(
    private val context: Context
) {
    /**
     * Creates and returns a Room database builder for [FinanceDatabase].
     *
     * @return A [RoomDatabase.Builder] configured for [FinanceDatabase].
     */
    fun create(): RoomDatabase.Builder<FinanceDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(FinanceDatabase.DATABASE_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}