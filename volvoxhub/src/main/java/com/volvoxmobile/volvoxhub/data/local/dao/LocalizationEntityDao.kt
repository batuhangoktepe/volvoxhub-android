package com.volvoxmobile.volvoxhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity

@Dao
interface LocalizationEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localizationEntity: LocalizationEntity)

    @Query("DELETE FROM localization WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM localization LIMIT 0,1")
    suspend fun get(): LocalizationEntity?

    @Delete
    suspend fun delete(localizationEntity: LocalizationEntity)
}