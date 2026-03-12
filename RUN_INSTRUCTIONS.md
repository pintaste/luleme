# 运行说明

## 📱 如何在本机运行此应用

### 前置要求

由于本机未检测到 Android 开发环境，您需要先安装以下工具：

#### 1. 安装 Android Studio

1. 访问 [Android Studio 官网](https://developer.android.com/studio)
2. 下载并安装 Android Studio
3. 首次启动时，按照向导安装 Android SDK

#### 2. 配置环境（可选但推荐）

在 `~/.zshrc` 或 `~/.bash_profile` 中添加：

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
```

然后执行：
```bash
source ~/.zshrc  # 或 source ~/.bash_profile
```

---

## 🚀 运行方法

### 方法一：使用 Android Studio（推荐）

1. **打开项目**
   ```bash
   # 在 Android Studio 中：File -> Open
   # 选择项目目录：/Users/pin/R Migration/luleme
   ```

2. **等待 Gradle 同步完成**
   - Android Studio 会自动下载依赖
   - 第一次同步可能需要几分钟

3. **启动模拟器**
   - 点击顶部工具栏的设备下拉菜单
   - 选择 "Device Manager"
   - 创建一个新的虚拟设备（推荐 Pixel 6, API 33+）
   - 点击启动按钮

4. **运行应用**
   - 点击顶部工具栏的绿色运行按钮（▶️）
   - 或按快捷键 `Ctrl + R` (Mac: `Cmd + R`)

---

### 方法二：使用命令行（需要先在 Android Studio 构建一次）

```bash
# 进入项目目录
cd "/Users/pin/R Migration/luleme"

# 构建 Debug APK
./gradlew assembleDebug

# 查看可用设备
adb devices

# 安装到设备/模拟器
adb install app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.luleme/.MainActivity
```

---

### 方法三：安装到真实 Android 设备

如果您有 Android 手机：

1. **在手机上启用开发者选项**
   - 进入 设置 -> 关于手机
   - 连续点击"版本号"7次
   - 返回设置，找到"开发者选项"
   - 开启"USB 调试"

2. **连接手机到电脑**
   - 使用 USB 数据线连接
   - 手机上允许 USB 调试授权

3. **在 Android Studio 中运行**
   - 点击运行按钮
   - 在设备列表中选择您的手机

---

## 🎯 测试"撤销起飞"功能

功能已实现完成，测试步骤：

1. **启动应用** - 查看主页
2. **点击"起飞"按钮** - 记录今日起飞
3. **观察界面变化**:
   - 今日状态卡片显示"已起飞 1 次"
   - 起飞按钮变为"又起飞了？"
   - **撤销按钮从底部平滑出现**（左侧，红色）
4. **点击"撤销"按钮**:
   - 起飞次数减 1
   - 状态卡片更新
   - 如果次数变为 0，撤销按钮自动隐藏
5. **多次测试**:
   - 连续起飞多次
   - 逐个撤销
   - 验证次数不会低于 0

---

## 🐛 故障排查

### Gradle 同步失败
```bash
# 清理并重新构建
./gradlew clean
./gradlew build --refresh-dependencies
```

### 模拟器启动失败
- 确保启用了 CPU 虚拟化（Intel VT-x 或 AMD-V）
- Mac M1/M2 芯片使用 ARM64 系统镜像

### 应用崩溃
- 检查 Logcat 输出（Android Studio 底部面板）
- 确保设备 API 级别 >= 26 (Android 8.0)

---

## 📦 构建 Release APK

```bash
# 设置签名环境变量（如果有 keystore）
export SIGNING_KEY_STORE_PATH=/path/to/keystore.jks
export SIGNING_STORE_PASSWORD=your_store_password
export SIGNING_KEY_ALIAS=your_key_alias
export SIGNING_KEY_PASSWORD=your_key_password

# 构建 Release APK
./gradlew assembleRelease

# APK 位置
# app/build/outputs/apk/release/app-release.apk
```

---

## 📝 注意事项

- 首次构建可能需要下载大量依赖（可能需要 10-20 分钟）
- 确保网络连接稳定
- 推荐使用 JDK 17 进行构建
- 最低支持 Android 8.0 (API 26)

---

## 💡 快速开始（如果已安装 Android Studio）

```bash
# 1. 用 Android Studio 打开项目
open -a "Android Studio" "/Users/pin/R Migration/luleme"

# 2. 等待 Gradle 同步完成

# 3. 点击运行按钮（绿色三角形）
```

完成！🎉
