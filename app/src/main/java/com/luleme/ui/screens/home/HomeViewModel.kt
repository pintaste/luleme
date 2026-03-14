package com.luleme.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luleme.domain.model.Record
import com.luleme.domain.repository.RecordRepository
import com.luleme.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val todayRecords: List<Record>,
        val weekCount: Int,
        val combatCount: Int,
        val todayTakeoffCount: Int,
        val todayCombatCount: Int,
        val monthTakeoffCount: Int,
        val monthCombatCount: Int,
        val totalTakeoffCount: Int,
        val totalCombatCount: Int,
        val age: Int,
        val overviewType: String?,
        val latestRecord: Record?
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = HomeUiState.Loading
            }
            try {
                val todayRecords = recordRepository.getTodayRecords()
                
                // Get this week's records
                val today = LocalDate.now()
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                
                val weekRecords = recordRepository.getRecordsBetween(
                    startOfWeek.format(DateTimeFormatter.ISO_DATE),
                    endOfWeek.format(DateTimeFormatter.ISO_DATE)
                )
                
                // Get latest record
                val latestRecord = recordRepository.getLatestRecord()
                
                val settings = userSettingsRepository.getSettings()
                val age = settings?.age ?: 25 // Default age
                
                // Calculate combat count for this week
                val combatCount = weekRecords.count { it.type == "作战" }
                
                // Calculate today's takeoff and combat counts
                val todayTakeoffCount = todayRecords.count { it.type == "起飞" }
                val todayCombatCount = todayRecords.count { it.type == "作战" }
                
                // Calculate this month's records
                val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
                val monthRecords = recordRepository.getRecordsBetween(
                    startOfMonth.format(DateTimeFormatter.ISO_DATE),
                    endOfMonth.format(DateTimeFormatter.ISO_DATE)
                )
                val monthTakeoffCount = monthRecords.count { it.type == "起飞" }
                val monthCombatCount = monthRecords.count { it.type == "作战" }
                
                // Calculate all time records
                val allRecords = recordRepository.getAllRecords()
                val totalTakeoffCount = allRecords.count { it.type == "起飞" }
                val totalCombatCount = allRecords.count { it.type == "作战" }

                _uiState.value = HomeUiState.Success(
                    todayRecords = todayRecords,
                    weekCount = weekRecords.size,
                    combatCount = combatCount,
                    todayTakeoffCount = todayTakeoffCount,
                    todayCombatCount = todayCombatCount,
                    monthTakeoffCount = monthTakeoffCount,
                    monthCombatCount = monthCombatCount,
                    totalTakeoffCount = totalTakeoffCount,
                    totalCombatCount = totalCombatCount,
                    age = age,
                    overviewType = settings?.overviewType,
                    latestRecord = latestRecord
                )
            } catch (e: Exception) {
                // If we are already showing data (Success), don't replace it with an error screen on refresh failure.
                // Only show Error state if we have nothing to show.
                if (_uiState.value !is HomeUiState.Success) {
                    _uiState.value = HomeUiState.Error(e.message ?: "未知错误")
                } else {
                    // TODO: In a real app, we might want to emit a one-time event (like a Snackbar) here
                    // to notify the user that the refresh failed, without destroying the UI.
                    e.printStackTrace()
                }
            }
        }
    }

    fun recordToday(type: String = "起飞") {
        viewModelScope.launch {
            try {
                recordRepository.addRecord(type)
                loadData(showLoading = false)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun undoTodayRecord() {
        viewModelScope.launch {
            try {
                recordRepository.deleteLatestTodayRecord()
                loadData(showLoading = false)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun saveOverviewType(overviewType: String) {
        viewModelScope.launch {
            try {
                val settings = userSettingsRepository.getSettings()
                if (settings != null) {
                    val updatedSettings = settings.copy(overviewType = overviewType)
                    userSettingsRepository.saveSettings(updatedSettings)
                } else {
                    // Create default settings if none exist
                    val defaultSettings = com.luleme.domain.model.UserSettings(
                        age = 25, // Default age
                        birthYear = null,
                        gender = null,
                        lockEnabled = false,
                        pinHash = null,
                        overviewType = overviewType
                    )
                    userSettingsRepository.saveSettings(defaultSettings)
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}
