package com.luleme.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Man
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.luleme.BuildConfig
import com.luleme.ui.components.CuteSwitch
import com.luleme.ui.components.SettingGroup
import com.luleme.ui.components.SettingItem
import com.luleme.ui.components.pin.NumPad
import com.luleme.ui.components.pin.PinDots
import com.luleme.ui.theme.CutePink
import com.luleme.ui.theme.SecondaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Pin Dialog States
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showVerifyPinDialog by remember { mutableStateOf(false) }
    var verifyAction by remember { mutableStateOf<(() -> Unit)?>(null) } 

    // Export/Import Launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val json = viewModel.getAllRecordsJson()
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(json.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "数据导出成功 ✨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val json = stream.bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
                        if (viewModel.restoreData(json)) {
                            Toast.makeText(context, "数据恢复成功 ✨", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "数据格式不对哦 😣", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "恢复失败了 😣", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("要清空所有数据吗？") },
            text = { Text("这个操作不能撤销哦，所有记录都会消失。") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                        Toast.makeText(context, "数据已清空", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("确认清空") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("再想想") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showSetPinDialog) {
        PinSetupDialog(
            onPinSet = { pin ->
                viewModel.setPin(pin)
                showSetPinDialog = false
                Toast.makeText(context, "密码设置成功 🔒", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showSetPinDialog = false }
        )
    }

    if (showVerifyPinDialog) {
        PinVerifyDialog(
            onVerify = { pin ->
                if (viewModel.verifyPin(pin)) {
                    showVerifyPinDialog = false
                    verifyAction?.invoke()
                    true
                } else {
                    false 
                }
            },
            onDismiss = { showVerifyPinDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)
    ) {
        item {
            Text(
                text = "设置",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        item {
            SettingGroup(title = "个人信息") {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 年龄显示和编辑
                    var showEditDialog by remember { mutableStateOf(false) }
                    
                    // 年龄显示
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 根据性别显示不同的图标和颜色
                        val (genderIcon, iconTint) = when (uiState.gender) {
                            "男" -> Pair(Icons.Rounded.Man, MaterialTheme.colorScheme.primary)
                            "女" -> Pair(Icons.Rounded.Woman, CutePink)
                            else -> Pair(Icons.Rounded.Face, SecondaryLight)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = genderIcon,
                                contentDescription = null,
                                tint = iconTint
                            )
                        }
                        Spacer(modifier = Modifier.padding(12.dp))
                        Text(
                            text = "年龄: ${uiState.age} 岁",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    showEditDialog = true
                                }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "编辑个人信息",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // 个人信息编辑对话框
                    if (showEditDialog) {
                        var selectedYear by remember { mutableStateOf(uiState.birthYear ?: 2000) }
                        var selectedGender by remember { mutableStateOf(uiState.gender ?: "") }
                        AlertDialog(
                            onDismissRequest = { showEditDialog = false },
                            title = { Text("编辑个人信息") },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 出生年份选择
                                    Column {
                                        Text("出生年份:")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("当前选择: $selectedYear")
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Slider(
                                            value = selectedYear.toFloat(),
                                            onValueChange = { selectedYear = it.toInt() },
                                            valueRange = 1900f..2026f,
                                            steps = 125,
                                            colors = SliderDefaults.colors(
                                                thumbColor = MaterialTheme.colorScheme.primary,
                                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    }
                                    
                                    // 性别选择
                                    Column {
                                        Text("性别:")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            listOf("男", "女", "其他").forEach { gender ->
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (selectedGender == gender) {
                                                                MaterialTheme.colorScheme.primary
                                                            } else {
                                                                MaterialTheme.colorScheme.surfaceVariant
                                                            },
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .clickable {
                                                            selectedGender = gender
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        text = gender,
                                                        color = if (selectedGender == gender) {
                                                            MaterialTheme.colorScheme.onPrimary
                                                        } else {
                                                            MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showEditDialog = false
                                        viewModel.updateBirthYear(selectedYear)
                                        viewModel.updateGender(selectedGender)
                                    }
                                ) {
                                    Text("确认")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showEditDialog = false }) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                }
            }
        }

        item {
            SettingGroup(title = "安全与隐私") {
                SettingItem(
                    icon = Icons.Rounded.Lock,
                    title = "应用锁",
                    subtitle = "保护你的隐私记录",
                    iconTint = CutePink,
                    trailingContent = {
                        CuteSwitch(
                            checked = uiState.lockEnabled,
                            onCheckedChange = { 
                                if (it) {
                                    if (uiState.hasPin) {
                                        verifyAction = { viewModel.toggleLock(true) }
                                        showVerifyPinDialog = true
                                    } else {
                                        showSetPinDialog = true
                                    }
                                } else {
                                    verifyAction = { viewModel.toggleLock(false) }
                                    showVerifyPinDialog = true
                                }
                            }
                        )
                    },
                    onClick = { /* Toggle logic handled by switch, but could also be here */ }
                )
                
                if (uiState.lockEnabled) {
                    SettingItem(
                        icon = Icons.Rounded.Password,
                        title = "修改密码",
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            verifyAction = { showSetPinDialog = true }
                            showVerifyPinDialog = true
                        }
                    )
                }
            }
        }

        item {
            SettingGroup(title = "数据管理") {
                SettingItem(
                    icon = Icons.Rounded.Download,
                    title = "备份数据",
                    subtitle = "用于数据迁移等场景",
                    iconTint = SecondaryLight,
                    onClick = { exportLauncher.launch("luleme_data.json") }
                )
                
                SettingItem(
                    icon = Icons.Rounded.Upload,
                    title = "恢复数据",
                    subtitle = "从 JSON 文件导入",
                    iconTint = MaterialTheme.colorScheme.primary,
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/json", "text/plain")) }
                )
                
                SettingItem(
                    icon = Icons.Rounded.Delete,
                    title = "清空所有数据",
                    subtitle = "慎重操作",
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = { showClearDialog = true }
                )
            }
        }
        
        item {
            Text(
                text = "Luleme v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clickable { uriHandler.openUri("https://github.com/sky22333/luleme") },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun PinVerifyDialog(
    onVerify: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(100)
            if (onVerify(pin)) {
                // Success handled by parent
            } else {
                error = "密码不对哦"
                pin = ""
                delay(1000)
                error = ""
            }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("验证密码 🔒", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (error.isNotEmpty()) error else "请输入当前密码",
                    color = if (error.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                PinDots(length = 4, inputLength = pin.length)
                Spacer(modifier = Modifier.weight(1f))
                NumPad(
                    onNumberClick = { if (pin.length < 4) pin += it },
                    onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) { Text("取消") }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PinSetupDialog(
    onPinSet: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var firstPin by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("设置新密码") }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(100)
            if (step == 1) {
                firstPin = pin
                pin = ""
                step = 2
                title = "请再次输入"
            } else {
                if (pin == firstPin) {
                    onPinSet(pin)
                } else {
                    error = "两次不一样哦"
                    pin = ""
                    firstPin = ""
                    step = 1
                    title = "设置新密码"
                    delay(1000)
                    error = ""
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (error.isNotEmpty()) error else if (step == 1) "设置一个4位数字密码" else "确认密码",
                    color = if (error.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                PinDots(length = 4, inputLength = pin.length)
                Spacer(modifier = Modifier.weight(1f))
                NumPad(
                    onNumberClick = { if (pin.length < 4) pin += it },
                    onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) { Text("取消") }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=800dp")
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
