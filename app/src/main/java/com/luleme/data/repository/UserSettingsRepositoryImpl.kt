package com.luleme.data.repository

import com.luleme.data.local.dao.UserSettingsDao
import com.luleme.data.local.entity.UserSettingsEntity
import com.luleme.domain.model.UserSettings
import com.luleme.domain.repository.UserSettingsRepository
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val dao: UserSettingsDao
) : UserSettingsRepository {

    override suspend fun getSettings(): UserSettings? {
        return dao.getSettings()?.toDomain()
    }

    override suspend fun saveSettings(settings: UserSettings) {
        dao.saveSettings(settings.toEntity())
    }

    private fun UserSettingsEntity.toDomain(): UserSettings {
        return UserSettings(
            age = this.age,
            birthYear = this.birthYear,
            gender = this.gender,
            lockEnabled = this.lockEnabled,
            pinHash = this.pinHash,
            overviewType = this.overviewType
        )
    }

    private fun UserSettings.toEntity(): UserSettingsEntity {
        return UserSettingsEntity(
            age = this.age,
            birthYear = this.birthYear,
            gender = this.gender,
            lockEnabled = this.lockEnabled,
            pinHash = this.pinHash,
            overviewType = this.overviewType
        )
    }
}
