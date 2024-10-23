package com.volvoxmobile.volvoxhub.data.local.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localization")
data class LocalizationEntity(
    @PrimaryKey val id: Int,
    val localizationUrl: String,
    val localizations: Map<String, String>,
) {
    companion object {
        const val DEFAULT_LOCALIZATION_ID = 1
    }
}
