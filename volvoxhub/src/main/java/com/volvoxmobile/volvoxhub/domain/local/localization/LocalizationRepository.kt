package com.volvoxmobile.volvoxhub.domain.local.localization

import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity

interface LocalizationRepository {
    suspend fun save(localizationEntity: LocalizationEntity)
    suspend fun get(): LocalizationEntity?
}