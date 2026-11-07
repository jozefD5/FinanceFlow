package com.jd.financeflow.features.finance.data.localStorageDataSource

import com.jd.financeflow.features.core.domain.result.DataError
import com.jd.financeflow.features.core.domain.result.Result
import com.jd.financeflow.features.core.data.localStorageDataSource.database.FinanceDao
import com.jd.financeflow.features.finance.data.mappers.toEntity
import com.jd.financeflow.features.finance.data.mappers.toFinance
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * A data source for local storage operations related to [Finance] objects.
 *
 * @property financeDao The data access object for [Finance] entities.
 */
class LocalStorageDataSource(
    private val financeDao: FinanceDao
) {
    /**
     * Retrieves all [Finance] objects from the database.
     *
     * @return A list of [Finance] objects on success, or a [DataError.Local] error otherwise.
     */
    suspend fun getAll(): Result<List<Finance>, DataError.Local> {
        return runCatching {
            val financeEntities = financeDao.getAll()
            Result.Success(financeEntities.map { it.toFinance() })
        }.getOrElse {
            Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
        }
    }

    /**
     * Retrieves a [Finance] object by its unique identifier.
     *
     * @param id The unique identifier of the [Finance] object to retrieve.
     * @return The [Finance] object with the specified identifier on success, or a
     * [DataError.Local] error otherwise.
     */
    suspend fun getById(id: Int): Result<Finance, DataError.Local> {
        return runCatching {
            val financeEntity = financeDao.getById(id)
            if (financeEntity != null) {
                Result.Success(financeEntity.toFinance())
            } else {
                Result.Error(DataError.Local.INVALID_ARGUMENT)
            }
        }.getOrElse {
            Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
        }
    }

    /**
     * Inserts a new [Finance] object into the database.
     *
     * @param finance The [Finance] object to insert.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun insert(finance: Finance): Result<Unit, DataError.Local> {
        return runCatching {
            financeDao.insert(finance.toEntity())
            Result.Success(Unit)
        }.getOrElse {
            Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
        }
    }

    /**
     * Updates an existing [Finance] object in the database.
     *
     * @param finance The [Finance] object to update.
     * @return A empty [Result] indicating the success, or a [DataError.Local] error otherwise.
     */
    suspend fun update(finance: Finance): Result<Unit, DataError.Local> {
        return runCatching {
            financeDao.update(finance.toEntity())
            Result.Success(Unit)
        }.getOrElse {
            Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
        }
    }

    /**
     * Deletes a [Finance] object from the database.
     *
     * @param finance The [Finance] object to delete.
     * @return An empty [Result] indicating the success, or a [DataError.Local] error otherwise.
     */
    suspend fun delete(finance: Finance): Result<Unit, DataError.Local> {
        return runCatching {
            financeDao.delete(finance.toEntity())
            Result.Success(Unit)
        }.getOrElse {
            Result.Error(DataError.Local.STORAGE_UNAVAILABLE)
        }
    }
}