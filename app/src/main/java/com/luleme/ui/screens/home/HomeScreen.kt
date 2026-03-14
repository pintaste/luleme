package com.luleme.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luleme.domain.model.Record
import com.luleme.ui.components.CuteCard
import com.luleme.ui.theme.CuteOrange
import com.luleme.ui.theme.CutePink
import com.luleme.ui.theme.CuteYellow
import com.luleme.ui.theme.CuteBlue
import com.luleme.ui.theme.SecondaryLight
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 当组件初始化时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadData(showLoading = false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "哎呀，出错了: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onRecordClick = { viewModel.recordToday("起飞") },
                    onCombatClick = { viewModel.recordToday("作战") },
                    onUndoClick = { viewModel.undoTodayRecord() },
                    onSaveOverviewType = { viewModel.saveOverviewType(it) }
                )
            }
        }
    }
}

enum class OverviewType { TODAY, WEEK, MONTH, ALL }

@Composable
fun HomeContent(
    state: HomeUiState.Success,
    onRecordClick: () -> Unit,
    onCombatClick: () -> Unit,
    onUndoClick: () -> Unit,
    onSaveOverviewType: (String) -> Unit
) {
    val hasRecordedToday = state.todayRecords.isNotEmpty()
    val takeoffCount = state.weekCount - state.combatCount
    val initialOverview = state.overviewType?.let {
        when (it) {
            "TODAY" -> OverviewType.TODAY
            "WEEK" -> OverviewType.WEEK
            "MONTH" -> OverviewType.MONTH
            "ALL" -> OverviewType.ALL
            else -> OverviewType.TODAY
        }
    } ?: OverviewType.TODAY
    var currentOverview by remember { mutableStateOf(initialOverview) }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp), // Space for FAB
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header with Greeting
        item {
            HeaderSection()
        }

        // 2. Main Status Card
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                TodayStatusCard(todayCount = state.todayTakeoffCount + state.todayCombatCount)
            }
        }

        // 4. Stats Section (4-grid layout)
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = when (currentOverview) {
                                    OverviewType.TODAY -> "今日概览"
                                    OverviewType.WEEK -> "本周概览"
                                    OverviewType.MONTH -> "本月概览"
                                    OverviewType.ALL -> "全部概览"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            IconButton(
                                onClick = { 
                                    currentOverview = when (currentOverview) {
                                        OverviewType.TODAY -> OverviewType.WEEK
                                        OverviewType.WEEK -> OverviewType.MONTH
                                        OverviewType.MONTH -> OverviewType.ALL
                                        OverviewType.ALL -> OverviewType.TODAY
                                    }
                                    onSaveOverviewType(currentOverview.name)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SwapHoriz,
                                    contentDescription = "切换概览",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    when (currentOverview) {
                        OverviewType.TODAY -> {
                            // 今日概览：四宫格分别是 起飞 作战 上次发射 状态
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "起飞",
                                        value = "${state.todayTakeoffCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.FlightTakeoff,
                                        iconTint = CuteBlue
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "作战",
                                        value = "${state.todayCombatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.SportsEsports,
                                        iconTint = CutePink
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (state.latestRecord != null) {
                                        LastTakeoffMiniCard(latestRecord = state.latestRecord)
                                    } else {
                                        // Use StatsCard for consistent sizing
                                        StatsCard(
                                            title = "上次发射",
                                            value = "暂无记录",
                                            unit = "",
                                            icon = Icons.Rounded.FlightTakeoff,
                                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "状态",
                                        value = if (hasRecordedToday) "贤者模式" else "活跃",
                                        unit = "",
                                        icon = Icons.Rounded.Favorite,
                                        iconTint = CutePink
                                    )
                                }
                            }
                        }
                        OverviewType.WEEK -> {
                            // 本周概览：四宫格分别是 起飞 作战 总计 上次发射
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "起飞",
                                        value = "${takeoffCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.FlightTakeoff,
                                        iconTint = CuteBlue
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "作战",
                                        value = "${state.combatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.SportsEsports,
                                        iconTint = CutePink
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "总计",
                                        value = "${state.weekCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.Star,
                                        iconTint = CuteYellow
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    if (state.latestRecord != null) {
                                        LastTakeoffMiniCard(latestRecord = state.latestRecord)
                                    } else {
                                        // Use StatsCard for consistent sizing
                                        StatsCard(
                                            title = "上次发射",
                                            value = "暂无记录",
                                            unit = "",
                                            icon = Icons.Rounded.FlightTakeoff,
                                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                        OverviewType.MONTH -> {
                            // 本月概览：四宫格分别是 起飞 作战 总计 上次发射
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "起飞",
                                        value = "${state.monthTakeoffCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.FlightTakeoff,
                                        iconTint = CuteBlue
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "作战",
                                        value = "${state.monthCombatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.SportsEsports,
                                        iconTint = CutePink
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "总计",
                                        value = "${state.monthTakeoffCount + state.monthCombatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.Star,
                                        iconTint = CuteYellow
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    if (state.latestRecord != null) {
                                        LastTakeoffMiniCard(latestRecord = state.latestRecord)
                                    } else {
                                        // Use StatsCard for consistent sizing
                                        StatsCard(
                                            title = "上次发射",
                                            value = "暂无记录",
                                            unit = "",
                                            icon = Icons.Rounded.FlightTakeoff,
                                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                        OverviewType.ALL -> {
                            // 全部概览：四宫格分别是 起飞 作战 总计 上次发射
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "起飞",
                                        value = "${state.totalTakeoffCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.FlightTakeoff,
                                        iconTint = CuteBlue
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "作战",
                                        value = "${state.totalCombatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.SportsEsports,
                                        iconTint = CutePink
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    StatsCard(
                                        title = "总计",
                                        value = "${state.totalTakeoffCount + state.totalCombatCount}",
                                        unit = "次",
                                        icon = Icons.Rounded.Star,
                                        iconTint = CuteYellow
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    if (state.latestRecord != null) {
                                        LastTakeoffMiniCard(latestRecord = state.latestRecord)
                                    } else {
                                        // Use StatsCard for consistent sizing
                                        StatsCard(
                                            title = "上次发射",
                                            value = "暂无记录",
                                            unit = "",
                                            icon = Icons.Rounded.FlightTakeoff,
                                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Health Tip
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                HealthTipCard(
                    frequency = state.weekCount,
                    age = state.age,
                    todayCount = state.todayRecords.size
                )
            }
        }
    }

    // Floating Action Buttons
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 撤销按钮 - 只在有今日记录时显示
            AnimatedVisibility(
                visible = hasRecordedToday,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(animationSpec = tween(durationMillis = 200)) { it },
                exit = fadeOut(animationSpec = tween(durationMillis = 200)) + slideOutVertically(animationSpec = tween(durationMillis = 200)) { it }
            ) {
                UndoButton(
                    todayCount = state.todayRecords.size,
                    onUndo = onUndoClick
                )
            }

            // 起飞按钮
            TakeoffButton(
                hasRecordedToday = hasRecordedToday,
                onTakeoff = onRecordClick
            )

            // 作战按钮
            CombatButton(
                hasRecordedToday = hasRecordedToday,
                onCombat = onCombatClick
            )
        }
    }
}

@Composable
fun UndoButton(
    todayCount: Int,
    onUndo: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isUndoing by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isUndoing) 0.85f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "undo_button_scale"
    )

    val handleUndo = {
        if (!isUndoing && todayCount > 0) {
            isUndoing = true

            scope.launch {
                delay(200)
                onUndo()
                delay(100)
                isUndoing = false
            }
        }
    }

    FloatingActionButton(
        onClick = { handleUndo() },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Undo,
            contentDescription = "撤销起飞",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TakeoffButton(
    hasRecordedToday: Boolean,
    onTakeoff: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isTakingOff by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isTakingOff) 0.85f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )

    val handleTakeoff = {
        if (!isTakingOff) {
            isTakingOff = true
            
            scope.launch {
                // 1. Play the scale animation first
                delay(200) // Shorter delay for more responsive feel
                
                // 2. Commit the record
                onTakeoff()
                
                // 3. Reset animation state after UI settles
                delay(200)
                isTakingOff = false
            }
        }
    }

    if (!hasRecordedToday) {
        ExtendedFloatingActionButton(
            onClick = { handleTakeoff() },
            containerColor = CuteBlue.copy(alpha = 0.8f),
            contentColor = Color.White,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 24.dp)
                .scale(scale),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                defaultElevation = 2.dp,
                pressedElevation = 1.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.FlightTakeoff, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "起飞！",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }
        }
    } else {
        Button(
            onClick = { handleTakeoff() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                contentColor = CuteBlue
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 1.dp
            ),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 12.dp)
                .scale(scale)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.FlightTakeoff, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("起飞！", maxLines = 1, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CombatButton(
    hasRecordedToday: Boolean,
    onCombat: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isCombatting by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCombatting) 0.85f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "combat_button_scale"
    )

    val handleCombat = {
        if (!isCombatting) {
            isCombatting = true
            
            scope.launch {
                // 1. Play the scale animation first
                delay(200) // Shorter delay for more responsive feel
                
                // 2. Commit the record
                onCombat()
                
                // 3. Reset animation state after UI settles
                delay(200)
                isCombatting = false
            }
        }
    }

    if (!hasRecordedToday) {
        ExtendedFloatingActionButton(
            onClick = { handleCombat() },
            containerColor = CutePink.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 24.dp)
                .scale(scale),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                defaultElevation = 2.dp,
                pressedElevation = 1.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.SportsEsports, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "作战！",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }
        }
    } else {
        Button(
            onClick = { handleCombat() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                contentColor = CutePink
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 1.dp
            ),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 12.dp)
                .scale(scale)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.SportsEsports, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("作战！", maxLines = 1, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA))
    val greeting = getGreetingMessage()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            Icon(
                    imageVector = getGreetingIcon(),
                    contentDescription = null,
                    tint = if (getGreetingIcon() == Icons.Rounded.NightsStay) MaterialTheme.colorScheme.primary else CuteOrange,
                    modifier = Modifier.size(28.dp)
                )
        }
    }
}

fun getGreetingMessage(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 0..4 -> "凌晨好！机长"
        in 5..10 -> "早上好！机长"
        in 11..12 -> "中午好！机长"
        in 13..17 -> "下午好！机长"
        else -> "晚上好！机长"
    }
}

fun getGreetingIcon() = when (LocalTime.now().hour) {
    in 0..4 -> Icons.Rounded.NightsStay
    in 5..10 -> Icons.Rounded.WbSunny
    in 11..12 -> Icons.Rounded.WbSunny
    in 13..17 -> Icons.Rounded.WbSunny
    else -> Icons.Rounded.NightsStay
}

@Composable
fun TodayStatusCard(todayCount: Int) {
    val hasRecordedToday = todayCount > 0
    val gradientColors = if (hasRecordedToday) {
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    } else {
        listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Brush.linearGradient(gradientColors))
            .padding(vertical = 24.dp, horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.3f), MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasRecordedToday) Icons.Rounded.Check else Icons.Rounded.Star,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (hasRecordedToday) Color.White else MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.size(20.dp))
            
            Column {
                Text(
                    text = if (hasRecordedToday) "今日已发射 $todayCount 次 ✨" else "今日还没发射",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (hasRecordedToday) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (hasRecordedToday) "保持好心情~" else "别忘了爱自己哦",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (hasRecordedToday) Color.White.copy(alpha = 0.9f) 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    CuteCard {
        Column(
            modifier = Modifier.padding(6.dp).height(88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 数值行
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = if (value == "暂无记录") {
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        } else if (value in listOf("活跃", "贤者模式")) {
                            MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                    if (unit.isNotEmpty()) {
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthTipCard(frequency: Int, age: Int, todayCount: Int) {
    val recommended = getRecommendedWeeklyFrequency(age)
    val maxRecommended = recommended.last
    
    val message = when {
        todayCount >= 2 -> "今天发射有点多次啦，注意身体哦机长 ✈️"
        todayCount == 1 -> "今天已经发射啦，心情不错吧~ ✨"
        frequency > maxRecommended -> "最近有点频繁呢，注意劳逸结合哦 💙"
        else -> "节奏很健康！继续保持~ ✨"
    }
    
    val isHighFreq = todayCount >= 2 || frequency > maxRecommended
    val backgroundColor = if (isHighFreq) MaterialTheme.colorScheme.tertiaryContainer 
                         else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isHighFreq) MaterialTheme.colorScheme.onTertiaryContainer 
                        else MaterialTheme.colorScheme.onSecondaryContainer
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor)
            .padding(vertical = 24.dp, horizontal = 24.dp)
    ) {
        Column {
            Text(
                text = "💡 健康小贴士",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}

fun getRecommendedWeeklyFrequency(age: Int): IntRange {
    return when (age) {
        in 18..25 -> 2..3
        in 26..35 -> 1..2
        in 36..45 -> 1..1
        else -> 1..1
    }
}

@Composable
fun LastTakeoffMiniCard(latestRecord: Record) {
    val latestDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(latestRecord.timestamp),
        ZoneId.systemDefault()
    )
    val formattedDate = latestDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA))
    
    val now = LocalDateTime.now()
    val period = java.time.Period.between(latestDateTime.toLocalDate(), now.toLocalDate())
    val duration = java.time.Duration.between(latestDateTime, now)
    val years = period.years
    val months = period.months
    val days = period.days
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    
    val timeSinceText = when {
        years > 0 -> "${years}年前"
        months > 0 -> "${months}个月前"
        days > 2 -> "${days}天前"
        days == 2 -> "前天"
        days == 1 -> "昨天"
        hours > 0 -> "${hours}小时前"
        minutes > 0 -> "${minutes}分钟前"
        seconds > 0 -> "${seconds}秒前"
        else -> "刚刚"
    }
    
    val isCombat = latestRecord.type == "作战"
    val title = if (isCombat) "上次作战" else "上次发射"
    val icon = if (isCombat) Icons.Rounded.SportsEsports else Icons.Rounded.FlightTakeoff
    val iconTint = if (isCombat) CutePink else CuteBlue
    
    CuteCard {
        Column(
            modifier = Modifier.padding(6.dp).height(88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 日期行
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
            
            // 时间间隔行
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = timeSinceText,
                    style = MaterialTheme.typography.labelSmall,
                    color = iconTint
                )
            }
        }
    }
}

@Composable
fun LastTakeoffCard(latestRecord: Record) {
    val latestDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(latestRecord.timestamp),
        ZoneId.systemDefault()
    )
    val formattedDate = latestDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA))
    
    val now = LocalDateTime.now()
    val period = java.time.Period.between(latestDateTime.toLocalDate(), now.toLocalDate())
    val duration = java.time.Duration.between(latestDateTime, now)
    val years = period.years
    val months = period.months
    val days = period.days
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    
    val timeSinceText = when {
        years > 0 -> "${years}年前"
        months > 0 -> "${months}个月前"
        days > 2 -> "${days}天前"
        days == 2 -> "前天"
        days == 1 -> "昨天"
        hours > 0 -> "${hours}小时前"
        minutes > 0 -> "${minutes}分钟前"
        seconds > 0 -> "${seconds}秒前"
        else -> "刚刚"
    }
    
    CuteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FlightTakeoff,
                    contentDescription = "上次起飞",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "上次起飞",
                    style = MaterialTheme.typography.labelMedium,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = timeSinceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp")
@Composable
fun HomeScreenPreview() {
    val mockState = HomeUiState.Success(
        todayRecords = emptyList(),
        todayTakeoffCount = 0,
        todayCombatCount = 0,
        weekCount = 5,
        combatCount = 2,
        monthTakeoffCount = 15,
        monthCombatCount = 6,
        totalTakeoffCount = 100,
        totalCombatCount = 40,
        latestRecord = com.luleme.domain.model.Record(
            id = 1,
            timestamp = System.currentTimeMillis() - 3600000,
            date = "2026-03-14",
            type = "起飞"
        ),
        overviewType = "TODAY",
        age = 25
    )
    
    HomeContent(
        state = mockState,
        onRecordClick = {},
        onCombatClick = {},
        onUndoClick = {},
        onSaveOverviewType = {}
    )
}
