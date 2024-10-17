package com.volvoxmobile.volvoxhub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.volvoxmobile.volvoxhub.data.local.dao.LocalizationEntityDao
import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity

@Database(
    entities = [
        LocalizationEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalizationTypeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localizationEntityDao(): LocalizationEntityDao
}