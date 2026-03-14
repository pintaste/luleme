package com.luleme.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luleme.domain.model.Record
import com.luleme.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class StatisticsUiState(
    val weekData: Map<DayOfWeek, Int> = emptyMap(),
    val weekTakeoffData: Map<DayOfWeek, Int> = emptyMap(),
    val weekCombatData: Map<DayOfWeek, Int> = emptyMap(),
    val monthData: Map<LocalDate, Int> = emptyMap(),
    val monthTakeoffData: Map<LocalDate, Int> = emptyMap(),
    val monthCombatData: Map<LocalDate, Int> = emptyMap(),
    val allRecords: List<Record> = emptyList(),
    val takeoffCount: Int = 0,
    val combatCount: Int = 0,
    val totalCount: Int = 0,
    val maxStreak: Int = 0,
    val averageFrequency: Float = 0f,
    val loading: Boolean = false,
    val snackbarMessage: String? = null,
    val countdown: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState(loading = true))
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // 保存待撤销删除的记录
    private var pendingDeleteRecord: Record? = null
    private var countdownJob: Job? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            
            val allRecords = recordRepository.getAllRecords()
            val today = LocalDate.now()

            // Week Data
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekData = mutableMapOf<DayOfWeek, Int>()
            val weekTakeoffData = mutableMapOf<DayOfWeek, Int>()
            val weekCombatData = mutableMapOf<DayOfWeek, Int>()
            for (i in 0..6) {
                val date = startOfWeek.plusDays(i.toLong())
                val dateStr = date.format(DateTimeFormatter.ISO_DATE)
                val count = allRecords.count { it.date == dateStr }
                val takeoffCount = allRecords.count { it.date == dateStr && it.type == "起飞" }
                val combatCount = allRecords.count { it.date == dateStr && it.type == "作战" }
                weekData[date.dayOfWeek] = count
                weekTakeoffData[date.dayOfWeek] = takeoffCount
                weekCombatData[date.dayOfWeek] = combatCount
            }

            // Month Data (Current Month)
            val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
            val lengthOfMonth = today.lengthOfMonth()
            val monthData = mutableMapOf<LocalDate, Int>()
            val monthTakeoffData = mutableMapOf<LocalDate, Int>()
            val monthCombatData = mutableMapOf<LocalDate, Int>()
            for (i in 0 until lengthOfMonth) {
                val date = startOfMonth.plusDays(i.toLong())
                val dateStr = date.format(DateTimeFormatter.ISO_DATE)
                val count = allRecords.count { it.date == dateStr }
                val takeoffCount = allRecords.count { it.date == dateStr && it.type == "起飞" }
                val combatCount = allRecords.count { it.date == dateStr && it.type == "作战" }
                monthData[date] = count
                monthTakeoffData[date] = takeoffCount
                monthCombatData[date] = combatCount
            }

            // All Time Stats
            val totalCount = allRecords.size
            val takeoffCount = allRecords.count { it.type == "起飞" }
            val combatCount = allRecords.count { it.type == "作战" }
            val maxStreak = calculateMaxStreak(allRecords)
            
            val firstRecord = allRecords.minByOrNull { it.timestamp }
            val average = if (firstRecord != null) {
                val days = ChronoUnit.DAYS.between(LocalDate.parse(firstRecord.date), today) + 1
                val weeks = kotlin.math.ceil(days / 7.0).toFloat()
                totalCount.toFloat() / weeks
            } else {
                0f
            }

            _uiState.value = _uiState.value.copy(
                weekData = weekData,
                weekTakeoffData = weekTakeoffData,
                weekCombatData = weekCombatData,
                monthData = monthData,
                monthTakeoffData = monthTakeoffData,
                monthCombatData = monthCombatData,
                allRecords = allRecords.sortedByDescending { it.timestamp },
                takeoffCount = takeoffCount,
                combatCount = combatCount,
                totalCount = totalCount,
                maxStreak = maxStreak,
                averageFrequency = average,
                loading = false
            )
        }
    }

    private fun calculateMaxStreak(records: List<Record>): Int {
        if (records.isEmpty()) return 0
        
        val sortedDates = records.map { LocalDate.parse(it.date) }.distinct().sorted()
        var maxStreak = 0
        var currentStreak = 0
        
        for (i in 0 until sortedDates.size) {
            if (i == 0) {
                currentStreak = 1
            } else {
                val prev = sortedDates[i - 1]
                val curr = sortedDates[i]
                if (ChronoUnit.DAYS.between(prev, curr) == 1L) {
                    currentStreak++
                } else {
                    maxStreak = maxOf(maxStreak, currentStreak)
                    currentStreak = 1
                }
            }
        }
        maxStreak = maxOf(maxStreak, currentStreak)

        return maxStreak
    }

    fun deleteRecord(record: Record) {
        countdownJob?.cancel()
        viewModelScope.launch {
            // 保存记录以便撤销
            pendingDeleteRecord = record

            // 从UI中移除记录并设置消息
            _uiState.value = _uiState.value.copy(
                allRecords = _uiState.value.allRecords.filter { it.id != record.id },
                snackbarMessage = "已删除",
                countdown = 4 // SnackbarDuration.Short 约为 4 秒
            )

            // 开始倒计时
            countdownJob = viewModelScope.launch {
                for (i in 4 downTo 1) {
                    _uiState.value = _uiState.value.copy(countdown = i)
                    delay(1000)
                }
                _uiState.value = _uiState.value.copy(countdown = 0)
            }

            // 实际删除记录
            recordRepository.deleteRecord(record.id)

            // 重新加载统计数据
            loadData()
        }
    }

    fun undoDelete() {
        countdownJob?.cancel()
        viewModelScope.launch {
            pendingDeleteRecord?.let { record ->
                // 重新添加记录
                recordRepository.importRecords(listOf(record))

                // 清除待撤销记录
                pendingDeleteRecord = null

                // 清除Snackbar消息和倒计时
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = null,
                    countdown = 0
                )

                // 重新加载数据
                loadData()
            }
        }
    }

    fun clearSnackbarMessage() {
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(
            snackbarMessage = null,
            countdown = 0
        )
        pendingDeleteRecord = null
    }
    
    fun addRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.importRecords(listOf(record))
            loadData()
        }
    }

    suspend fun getMonthData(month: LocalDate): Map<LocalDate, Int> {
        val allRecords = recordRepository.getAllRecords()
        val startOfMonth = month.with(TemporalAdjusters.firstDayOfMonth())
        val lengthOfMonth = month.lengthOfMonth()
        val monthData = mutableMapOf<LocalDate, Int>()
        for (i in 0 until lengthOfMonth) {
            val date = startOfMonth.plusDays(i.toLong())
            val count = allRecords.count { it.date == date.format(DateTimeFormatter.ISO_DATE) }
            monthData[date] = count
        }
        return monthData
    }
}
