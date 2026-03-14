package com.luleme.domain.model

data class UserSettings(
    val age: Int,
    val birthYear: Int? = null,
    val gender: String? = null,
    val lockEnabled: Boolean,
    val pinHash: String?,
    val overviewType: String? = null
)
