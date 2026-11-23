package com.jd.financeflow.features.core.data.localDataSource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) for the [FinanceEntity] entity. Allows to access and manipulate
 * data in the database.
 */
@Dao
interface FinanceDao {
    /**
     * Retrieves all [FinanceEntity] objects from the database.
     *
     * @return A list of [FinanceEntity] objects.
     */
    @Query("SELECT * FROM financeentity ORDER BY id ASC")
    suspend fun getAll(): List<FinanceEntity>

    /**
     * Retrieves a [FinanceEntity] object by its unique identifier.
     *
     * @param id The unique identifier of the [FinanceEntity] object to retrieve.
     * @return The [FinanceEntity] object with the specified identifier
     */
    @Query("SELECT * FROM financeentity WHERE id = :id")
    suspend fun getById(id: Int): FinanceEntity?

    /**
     * Inserts a new [FinanceEntity] object into the database.
     *
     * @param item The [FinanceEntity] object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FinanceEntity)

    /**
     * Updates an existing [FinanceEntity] object in the database.
     *
     * @param item The [FinanceEntity] object to update.
     */
    @Update
    suspend fun update(item: FinanceEntity)

    /**
     * Deletes a [FinanceEntity] object from the database.
     *
     * @param item The [FinanceEntity] object to delete.
     */
    @Delete
    suspend fun delete(item: FinanceEntity)
}