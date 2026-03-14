package com.luleme.ui.screens.statistics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Surface
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.luleme.domain.model.Record
import com.luleme.ui.components.CuteCard
import com.luleme.ui.theme.CutePink
import com.luleme.ui.theme.CuteYellow
import com.luleme.ui.theme.CuteBlue
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("周视图", "月视图", "时间线", "全部")
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    var currentWeek by remember { mutableStateOf(LocalDate.now()) }
    var isDirectTabClick by remember { mutableStateOf(false) }

    // 监听Snackbar消息并显示
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "撤销",
                duration = SnackbarDuration.Short
            )

            when (result) {
                androidx.compose.material3.SnackbarResult.ActionPerformed -> {
                    // 用户点击了撤销按钮
                    viewModel.undoDelete()
                }
                androidx.compose.material3.SnackbarResult.Dismissed -> {
                    // Snackbar被自动关闭（超时）
                    viewModel.clearSnackbarMessage()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    action = {
                        data.visuals.actionLabel?.let { actionLabel ->
                            TextButton(onClick = { data.performAction() }) {
                                Text(actionLabel, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uiState.countdown > 0) {
                            Text(
                                text = "${uiState.countdown}s",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                        Text(
                            data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            if (index == 2) { // 时间线tab
                                isDirectTabClick = true
                                // 直接点击tab进入时间线时，清除选中日期
                                selectedDate = null
                            } else {
                                isDirectTabClick = false
                            }
                            selectedTab = index
                        },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // 计算当前周的数据
                    val startOfWeek = currentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val weekData = mutableMapOf<DayOfWeek, Int>()
                    for (i in 0..6) {
                        val date = startOfWeek.plusDays(i.toLong())
                        val count = uiState.allRecords.count { 
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()).toLocalDate() == date
                        }
                        weekData[date.dayOfWeek] = count
                    }
                    
                    WeekView(
                        weekData = weekData,
                        weekTakeoffData = uiState.weekTakeoffData,
                        weekCombatData = uiState.weekCombatData,
                        currentWeek = currentWeek,
                        onDayClick = { date ->
                            selectedDate = date
                            isDirectTabClick = false // 通过点击日期进入时间线
                            selectedTab = 2 // 切换到时间线视图
                        },
                        onPreviousWeek = { currentWeek = currentWeek.minusWeeks(1) },
                        onNextWeek = { currentWeek = currentWeek.plusWeeks(1) }
                    )
                }
                1 -> {
                    // 显示当前月份和历史月份（只显示当前月和之前的月份，不显示未来月份）
                    val monthsToShow = 3 // 显示当前月和前2个月
                    val months = mutableListOf<LocalDate>()
                    val monthDataMap = remember { mutableStateOf<Map<LocalDate, Map<LocalDate, Int>>>(emptyMap()) }
                    val monthTakeoffDataMap = remember { mutableStateOf<Map<LocalDate, Map<LocalDate, Int>>>(emptyMap()) }
                    val monthCombatDataMap = remember { mutableStateOf<Map<LocalDate, Map<LocalDate, Int>>>(emptyMap()) }
                    
                    // 计算要显示的月份：当前月和历史月份
                    for (i in 0 until monthsToShow) {
                        months.add(currentMonth.minusMonths(i.toLong()))
                    }
                    
                    // 当记录变化时重新加载月份数据
                    LaunchedEffect(months, uiState.allRecords) {
                        val newMonthDataMap = mutableMapOf<LocalDate, Map<LocalDate, Int>>()
                        val newMonthTakeoffDataMap = mutableMapOf<LocalDate, Map<LocalDate, Int>>()
                        val newMonthCombatDataMap = mutableMapOf<LocalDate, Map<LocalDate, Int>>()
                        months.forEach {
                            val monthRecords = uiState.allRecords.filter { record ->
                                LocalDate.parse(record.date).month == it.month && LocalDate.parse(record.date).year == it.year
                            }
                            val monthData = mutableMapOf<LocalDate, Int>()
                            val monthTakeoffData = mutableMapOf<LocalDate, Int>()
                            val monthCombatData = mutableMapOf<LocalDate, Int>()
                            
                            val startOfMonth = it.with(TemporalAdjusters.firstDayOfMonth())
                            val lengthOfMonth = it.lengthOfMonth()
                            
                            for (i in 0 until lengthOfMonth) {
                                val date = startOfMonth.plusDays(i.toLong())
                                val dateStr = date.format(DateTimeFormatter.ISO_DATE)
                                val count = monthRecords.count { record -> record.date == dateStr }
                                val takeoffCount = monthRecords.count { record -> record.date == dateStr && record.type == "起飞" }
                                val combatCount = monthRecords.count { record -> record.date == dateStr && record.type == "作战" }
                                monthData[date] = count
                                monthTakeoffData[date] = takeoffCount
                                monthCombatData[date] = combatCount
                            }
                            
                            newMonthDataMap[it] = monthData
                            newMonthTakeoffDataMap[it] = monthTakeoffData
                            newMonthCombatDataMap[it] = monthCombatData
                        }
                        monthDataMap.value = newMonthDataMap
                        monthTakeoffDataMap.value = newMonthTakeoffDataMap
                        monthCombatDataMap.value = newMonthCombatDataMap
                    }
                    
                    // 垂直滚动显示多个月份
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        months.forEach { month ->
                            item {
                                MonthView(
                                    monthData = monthDataMap.value.getOrDefault(month, emptyMap()),
                                    monthTakeoffData = monthTakeoffDataMap.value.getOrDefault(month, emptyMap()),
                                    monthCombatData = monthCombatDataMap.value.getOrDefault(month, emptyMap()),
                                    currentMonth = month,
                                    onDateClick = { date ->
                                        selectedDate = date
                                        isDirectTabClick = false // 通过点击日期进入时间线
                                        selectedTab = 2 // 切换到时间线视图
                                    },
                                    onLoadPreviousMonth = {}
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                2 -> {
                    var showAddDialog by remember { mutableStateOf(false) }
                    var selectedAddDate by remember { mutableStateOf<LocalDate?>(null) }
                    var selectedHour by remember { mutableStateOf(LocalTime.now().hour) }
                    var selectedMinute by remember { mutableStateOf(LocalTime.now().minute) }
                    var editingDate by remember { mutableStateOf<LocalDate?>(null) }
                    
                    // 添加一个状态来控制是否显示全部记录
                    var showAllRecords by remember { mutableStateOf(true) }
                    
                    // 当进入时间线视图时，重置状态
                    LaunchedEffect(selectedTab, selectedDate, isDirectTabClick) {
                        if (selectedTab == 2) {
                            // 如果有选中日期，显示该日期的记录
                            // 如果没有选中日期（直接点击tab或从其他视图切换回来），显示全部记录
                            showAllRecords = selectedDate == null
                        }
                    }
                    
                    TimelineView(
                        records = if (selectedDate != null && !showAllRecords) {
                            uiState.allRecords.filter {
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()).toLocalDate() == selectedDate
                            }
                        } else {
                            uiState.allRecords
                        },
                        onDeleteRecord = viewModel::deleteRecord,
                        onAddRecord = { date ->
                            selectedAddDate = date
                            editingDate = date
                            selectedHour = LocalTime.now().hour
                            selectedMinute = LocalTime.now().minute
                            showAddDialog = true
                        },
                        selectedDate = if (showAllRecords) null else selectedDate,
                        onToggleView = if (selectedDate != null) {
                            { showAllRecords = !showAllRecords }
                        } else {
                            null
                        },
                        showAll = showAllRecords,
                        onDateClick = { date ->
                            selectedDate = date
                            showAllRecords = false
                        }
                    )
                    
                    // 添加记录的对话框
                    if (showAddDialog && editingDate != null) {
                        var selectedType by remember { mutableStateOf("起飞") }
                        
                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = { Text("添加记录") },
                            text = {
                                Column {
                                    // 记录类型选择
                                    Text("记录类型:")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Button(
                                            onClick = { selectedType = "起飞" },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedType == "起飞") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (selectedType == "起飞") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Text("起飞")
                                        }
                                        Button(
                                            onClick = { selectedType = "作战" },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedType == "作战") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = if (selectedType == "作战") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Text("作战")
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // 日期选择器
                                    Text("选择日期:")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 年份选择
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.minusYears(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = "减少", modifier = Modifier.size(20.dp))
                                                }
                                                Text(
                                                    text = editingDate?.year.toString() ?: "",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.width(64.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.plusYears(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropUp, contentDescription = "增加", modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Text("年", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                                        }
                                        
                                        Text("年", style = MaterialTheme.typography.bodyMedium)
                                        
                                        // 月份选择
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.minusMonths(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = "减少", modifier = Modifier.size(20.dp))
                                                }
                                                Text(
                                                    text = editingDate?.monthValue.toString().padStart(2, '0'),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.width(48.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.plusMonths(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropUp, contentDescription = "增加", modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Text("月", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                                        }
                                        
                                        Text("月", style = MaterialTheme.typography.bodyMedium)
                                        
                                        // 日选择
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.minusDays(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = "减少", modifier = Modifier.size(20.dp))
                                                }
                                                Text(
                                                    text = editingDate?.dayOfMonth.toString().padStart(2, '0'),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.width(48.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                    onClick = {
                                                        editingDate = editingDate?.plusDays(1)
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropUp, contentDescription = "增加", modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Text("日", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // 时间选择器
                                    Text("选择时间:")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 小时选择
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = "减少", modifier = Modifier.size(20.dp))
                                                }
                                                Text(
                                                    text = selectedHour.toString().padStart(2, '0'),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.width(48.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                    onClick = {
                                                        selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropUp, contentDescription = "增加", modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Text("小时", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                                        }
                                        
                                        Text(":", style = MaterialTheme.typography.headlineMedium)
                                        
                                        // 分钟选择
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        selectedMinute = if (selectedMinute > 0) selectedMinute - 1 else 59
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropDown, contentDescription = "减少", modifier = Modifier.size(20.dp))
                                                }
                                                Text(
                                                    text = selectedMinute.toString().padStart(2, '0'),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    modifier = Modifier.width(48.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                IconButton(
                                                    onClick = {
                                                        selectedMinute = if (selectedMinute < 59) selectedMinute + 1 else 0
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Rounded.ArrowDropUp, contentDescription = "增加", modifier = Modifier.size(20.dp))
                                                }
                                            }
                                            Text("分钟", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        // 创建一个新记录
                                        val selectedTime = LocalTime.of(selectedHour, selectedMinute)
                                        val localDateTime = LocalDateTime.of(editingDate!!, selectedTime)
                                        val timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                        
                                        val newRecord = Record(
                                            id = System.currentTimeMillis(),
                                            timestamp = timestamp,
                                            date = editingDate!!.format(DateTimeFormatter.ISO_DATE),
                                            note = null,
                                            type = selectedType
                                        )
                                        // 添加记录
                                        viewModel.addRecord(newRecord)
                                        showAddDialog = false
                                    }
                                ) {
                                    Text("确定")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showAddDialog = false }
                                ) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                }
                3 -> AllTimeView(uiState)
            }
        }
    }
}

@Composable
fun TimelineView(
    records: List<Record>,
    onDeleteRecord: (Record) -> Unit,
    onAddRecord: (LocalDate) -> Unit,
    selectedDate: LocalDate? = null,
    onToggleView: (() -> Unit)? = null,
    showAll: Boolean = false,
    onDateClick: (LocalDate) -> Unit
) {
    // 按日期分组记录
    val recordsByDate = records.groupBy {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()).toLocalDate()
    }
    
    if (recordsByDate.isEmpty()) {
        // 如果没有记录，使用选中的日期或今天
        val displayDate = selectedDate ?: LocalDate.now()
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 显示日期标题
            item {
                DateHeader(
                    date = displayDate, 
                    onAddRecord = { onAddRecord(displayDate) },
                    onToggleView = onToggleView,
                    showAll = showAll,
                    onDateClick = onDateClick
                )
            }
            // 显示暂无记录的提示
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            recordsByDate.forEach { (date, dateRecords) ->
                // 按时间排序记录
                val sortedRecords = dateRecords.sortedBy { it.timestamp }
                
                // 显示日期标题
                item {
                    DateHeader(
                        date = date, 
                        onAddRecord = { onAddRecord(date) },
                        onToggleView = if (selectedDate != null) onToggleView else null,
                        showAll = showAll,
                        onDateClick = onDateClick
                    )
                }
                
                // 显示该日期的所有记录，并在记录之间显示时间间隔
                sortedRecords.forEachIndexed { index, record ->
                    // 如果不是第一条记录，显示与前一条记录的时间间隔
                    if (index > 0) {
                        val previousRecord = sortedRecords[index - 1]
                        val diff = record.timestamp - previousRecord.timestamp
                        val timeInterval = formatTimeInterval(diff)
                        
                        item {
                            TimeIntervalItem(interval = timeInterval)
                        }
                    }
                    
                    // 显示记录
                    item {
                        RecordItem(
                            record = record, 
                            onDeleteRecord = onDeleteRecord
                        )
                    }
                }
            }
        }
    }
}

// 格式化时间间隔
fun formatTimeInterval(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> "${days}天"
        hours > 0 -> "${hours}小时"
        minutes > 0 -> "${minutes}分钟"
        else -> "${seconds}秒"
    }
}

// 显示时间间隔的组件
@Composable
fun TimeIntervalItem(interval: String) {
    // 在Composable上下文中获取颜色值
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧单个点
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(dotColor, shape = CircleShape)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "间隔 $interval",
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        // 右侧单个点
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(dotColor, shape = CircleShape)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun DateHeader(
    date: LocalDate, 
    onAddRecord: () -> Unit, 
    onToggleView: (() -> Unit)? = null, 
    showAll: Boolean = false,
    onDateClick: (LocalDate) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE", Locale.CHINA)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onDateClick(date)
                    }
                )
                
                if (onToggleView != null) {
                    TextButton(
                        onClick = onToggleView,
                        modifier = Modifier.padding(0.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (showAll) Icons.Rounded.CalendarToday else Icons.Rounded.CalendarViewWeek,
                                contentDescription = if (showAll) "显示当天" else "显示全部",
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (showAll) "当天" else "全部",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            
            IconButton(onClick = onAddRecord) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "添加记录",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RecordItem(record: Record, onDeleteRecord: (Record) -> Unit) {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val isCombat = record.type == "作战"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isCombat) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateTime.format(formatter),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCombat) "作战" else "起飞",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCombat) CutePink else CuteBlue
                    )
                }
                if (!record.note.isNullOrEmpty()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            IconButton(onClick = { onDeleteRecord(record) }) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun AllTimeView(uiState: StatisticsUiState) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "总发射",
                value = "${uiState.totalCount}",
                icon = Icons.Rounded.Star,
                color = CuteYellow,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "最长连续",
                value = "${uiState.maxStreak}天",
                icon = Icons.Rounded.Favorite,
                color = CutePink,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "起飞次数",
                value = "${uiState.takeoffCount}",
                icon = Icons.Rounded.FlightTakeoff,
                color = CuteBlue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "作战次数",
                value = "${uiState.combatCount}",
                icon = Icons.Rounded.SportsEsports,
                color = CutePink,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CuteCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("记录统计", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                
                StatRow("本周平均", "%.1f 次/天".format(uiState.totalCount.toFloat() / 7))
                StatRow("本月累计", "${uiState.monthData.values.sum()} 次")
                StatRow("起飞占比", "%.1f%%".format(if (uiState.totalCount > 0) uiState.takeoffCount.toFloat() / uiState.totalCount * 100 else 0f))
                StatRow("作战占比", "%.1f%%".format(if (uiState.totalCount > 0) uiState.combatCount.toFloat() / uiState.totalCount * 100 else 0f))
                StatRow("活跃天数", "${uiState.allRecords.map { 
                    Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() 
                }.distinct().size} 天")
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = color)
            Text(title, style = MaterialTheme.typography.labelMedium, color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeekView(
    weekData: Map<DayOfWeek, Int>,
    weekTakeoffData: Map<DayOfWeek, Int>,
    weekCombatData: Map<DayOfWeek, Int>,
    currentWeek: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    // 计算当前周的开始和结束日期
    val startOfWeek = currentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = startOfWeek.plusDays(6)
    val weekRange = "${startOfWeek.monthValue}/${startOfWeek.dayOfMonth} - ${endOfWeek.monthValue}/${endOfWeek.dayOfMonth}"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousWeek) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = "上一周", modifier = Modifier.size(24.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                val total = weekData.values.sum()
                Text(
                    weekRange, 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$total",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onNextWeek) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = "下一周", modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (weekData.isNotEmpty()) {
            val maxCount = weekData.values.maxOrNull() ?: 1
            var selectedDay by remember { mutableStateOf<DayOfWeek?>(null) }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                DayOfWeek.entries.forEach { day ->
                    val count = weekData[day] ?: 0
                    val takeoffCount = weekTakeoffData[day] ?: 0
                    val combatCount = weekCombatData[day] ?: 0
                    val isSelected = selectedDay == day
                    val totalHeightFraction = if (maxCount > 0) count.toFloat() / maxCount else 0f
                    val takeoffHeightFraction = if (maxCount > 0) takeoffCount.toFloat() / maxCount else 0f
                    val combatHeightFraction = if (maxCount > 0) combatCount.toFloat() / maxCount else 0f
                    
                    var animatedTakeoffHeight by remember { mutableStateOf(0f) }
                    var animatedCombatHeight by remember { mutableStateOf(0f) }
                    
                    LaunchedEffect(takeoffCount) {
                        animatedTakeoffHeight = takeoffHeightFraction
                    }
                    
                    LaunchedEffect(combatCount) {
                        animatedCombatHeight = combatHeightFraction
                    }
                    
                    val animatedTakeoffFraction by animateFloatAsState(
                        targetValue = animatedTakeoffHeight,
                        animationSpec = tween(durationMillis = 800, delayMillis = day.ordinal * 100)
                    )
                    
                    val animatedCombatFraction by animateFloatAsState(
                        targetValue = animatedCombatHeight,
                        animationSpec = tween(durationMillis = 800, delayMillis = day.ordinal * 100 + 100)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedDay = if (isSelected) null else day
                                // 计算对应的LocalDate
                                val startOfWeek = currentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                val daysUntilTarget = day.ordinal - DayOfWeek.MONDAY.ordinal
                                val targetDate = startOfWeek.plusDays(daysUntilTarget.toLong())
                                onDayClick(targetDate)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f) // Fill available vertical space
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter // Align bar to bottom
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Combat bar (bottom)
                                if (combatCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(if (animatedCombatFraction < 0.05f) 0.05f else animatedCombatFraction.coerceAtLeast(0.02f))
                                            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                                            .background(MaterialTheme.colorScheme.error)
                                    )
                                }
                                
                                // Takeoff bar (top)
                                if (takeoffCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(if (animatedTakeoffFraction < 0.05f) 0.05f else animatedTakeoffFraction.coerceAtLeast(0.02f))
                                            .clip(if (combatCount > 0) RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp) else RoundedCornerShape(50.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                
                                // Always show count at bottom
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 计算当前周中该天的日期
                        val startOfWeek = currentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        val targetDate = startOfWeek.plusDays(day.ordinal.toLong())
                        val isToday = targetDate == LocalDate.now()
                        
                        Text(
                            text = day.getDisplayName(TextStyle.SHORT, Locale.CHINESE),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        
        // 图例
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.primary)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("起飞", style = MaterialTheme.typography.labelSmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("作战", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun MonthView(
    monthData: Map<LocalDate, Int>,
    monthTakeoffData: Map<LocalDate, Int>,
    monthCombatData: Map<LocalDate, Int>,
    currentMonth: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onLoadPreviousMonth: () -> Unit
) {
    val today = LocalDate.now()
    val startOfMonth = currentMonth.with(TemporalAdjusters.firstDayOfMonth())
    val firstDayOfWeek = startOfMonth.dayOfWeek
    
    // 计算日历网格的行数
    val daysInMonth = currentMonth.lengthOfMonth()
    val startOffset = firstDayOfWeek.ordinal
    val totalCells = startOffset + daysInMonth
    val rows = (totalCells + 6) / 7 // 每星期7天，向上取整
    
    // 星期标题
    val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
    
    // 计算当月总起飞次数
    val totalTakeoffs = monthData.values.sum()
    
    // 检查当前月份是否有记录
    val hasRecords = totalTakeoffs > 0
    
    Column(modifier = Modifier.padding(16.dp)) {
        CuteCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📅 ${currentMonth.year}年${currentMonth.monthValue}月发射足迹", 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "共 ${totalTakeoffs} 次", 
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 星期标题行
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdays.forEach {day ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 日历网格
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val dayIndex = row * 7 + col
                            val dayOfMonth = dayIndex - startOffset + 1
                            
                            val date = if (dayOfMonth in 1..daysInMonth) {
                                startOfMonth.plusDays((dayOfMonth - 1).toLong())
                            } else {
                                null
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clickable {
                                        if (date != null) {
                                            onDateClick(date)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (date != null) {
                                    val isToday = date == today
                                    val takeoffCount = monthTakeoffData[date] ?: 0
                                    val combatCount = monthCombatData[date] ?: 0
                                    
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxHeight()
                                    ) {
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Text(
                                                text = dayOfMonth.toString(),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            // 起飞点
                                            if (takeoffCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.primary,
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = takeoffCount.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            // 作战点
                                            if (combatCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .background(
                                                            CutePink,
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = combatCount.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                        if (takeoffCount == 0 && combatCount == 0) {
                                            // 为了保持高度一致，添加一个占位符
                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 添加行间距
                    if (row < rows - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                // 显示无记录提示
                if (!hasRecords) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "该月无记录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        

    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp")
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen()
}
