package com.jd.financeflow.features.finance.data.localDataSource

import androidx.room.Query

/**
 * Data Access Object (DAO) for the [FinanceEntity] entity. Allows to access and manipulate
 * data in the database.
 */
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
    suspend fun insert(item: FinanceEntity)

    /**
     * Updates an existing [FinanceEntity] object in the database.
     *
     * @param item The [FinanceEntity] object to update.
     */
    suspend fun update(item: FinanceEntity)

    /**
     * Deletes a [FinanceEntity] object from the database.
     *
     * @param item The [FinanceEntity] object to delete.
     */
    suspend fun delete(item: FinanceEntity)
}