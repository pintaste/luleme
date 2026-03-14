package com.luleme.domain.repository

import com.luleme.domain.model.Record

interface RecordRepository {
    suspend fun getTodayRecords(): List<Record>
    suspend fun getRecordsBetween(startDate: String, endDate: String): List<Record>
    suspend fun addRecord(type: String = "起飞", note: String? = null)
    suspend fun deleteLatestTodayRecord()
    suspend fun deleteRecord(id: Long)
    suspend fun clearAll()
    suspend fun getAllRecords(): List<Record>
    suspend fun importRecords(records: List<Record>)
    suspend fun getLatestRecord(): Record?
}
