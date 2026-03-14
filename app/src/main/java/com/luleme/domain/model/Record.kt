package com.luleme.domain.model

data class Record(
    val id: Long = 0,
    val timestamp: Long,
    val date: String,
    val type: String = "起飞", // 起飞或作战
    val note: String? = null
)
