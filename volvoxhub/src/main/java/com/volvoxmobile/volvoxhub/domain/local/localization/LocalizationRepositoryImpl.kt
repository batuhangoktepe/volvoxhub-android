package com.volvoxmobile.volvoxhub.domain.local.localization

import com.volvoxmobile.volvoxhub.data.local.dao.LocalizationEntityDao
import com.volvoxmobile.volvoxhub.data.local.model.db.LocalizationEntity

class LocalizationRepositoryImpl(private val localizationEntityDao: LocalizationEntityDao) : LocalizationRepository {

    override suspend fun save(localizationEntity: LocalizationEntity) = localizationEntityDao.save(localizationEntity = localizationEntity)
    override suspend fun get(): LocalizationEntity? = localizationEntityDao.get()
}