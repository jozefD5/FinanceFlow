package com.jd.financeflow.features.finance.data.localDataSource

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
    fun create(): RoomDatabase.Builder<FinanceDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(FinanceDatabase.DATABASE_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}