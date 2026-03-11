# 安装与测试说明

## ✅ 构建成功！

APK 已生成：`app/build/outputs/apk/debug/app-debug.apk` (16 MB)

---

## 📱 方式一：通过 ADB 安装（推荐）

### 1. 授权 USB 调试

如果手机上显示"允许 USB 调试"对话框：
- ✅ 勾选"一律允许使用这台计算机进行调试"
- ✅ 点击"允许"

### 2. 验证设备已授权

```bash
export PATH=$PATH:~/Library/Android/sdk/platform-tools
adb devices
```

应该显示：
```
List of devices attached
461QYGF7226BD	device    # ✅ 注意是 "device" 而不是 "unauthorized"
```

### 3. 安装并运行应用

```bash
# 设置 JAVA_HOME
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools

# 进入项目目录
cd "/Users/pin/R Migration/luleme"

# 安装到手机
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.luleme/.MainActivity
```

---

## 📂 方式二：手动安装

### 1. 将 APK 发送到手机

**使用 AirDrop（Mac 到 iPhone 不支持，仅限 Mac 之间）**
或
**通过其他方式传输：**

```bash
# 方式 A: 通过 Android File Transfer
# 1. 安装 Android File Transfer (https://www.android.com/filetransfer/)
# 2. 连接手机
# 3. 将 app-debug.apk 拖到手机的 Downloads 文件夹

# 方式 B: 通过 ADB 推送
adb push "app/build/outputs/apk/debug/app-debug.apk" /sdcard/Download/

# 方式 C: 通过云盘（Google Drive、百度网盘等）
# 将 APK 上传到云盘，然后在手机上下载
```

### 2. 在手机上安装

1. 打开手机的"文件管理器"
2. 找到 `Downloads` 文件夹
3. 点击 `app-debug.apk`
4. 如果提示"禁止安装未知来源应用"：
   - 进入 **设置 → 安全 → 安装未知应用**
   - 允许文件管理器安装应用
5. 点击"安装"

---

## 🎯 测试"撤销起飞"功能

安装完成后，测试新功能：

### 测试步骤

1. **启动应用**
   - 在手机上打开"撸了么"应用
   - 查看主页

2. **初始状态检查**
   - ✅ 应显示"今日还没起飞"
   - ✅ 底部中央有大的"起飞"按钮
   - ✅ **没有撤销按钮**

3. **点击"起飞"按钮**
   - ✅ 状态卡片变为"今日已起飞 1 次 ✨"
   - ✅ **左侧平滑出现红色"撤销"按钮**
   - ✅ 右侧起飞按钮变小，文字变为"又起飞了？"

4. **点击"撤销"按钮**
   - ✅ 起飞次数减 1（从 1 → 0）
   - ✅ 状态恢复为"今日还没起飞"
   - ✅ **撤销按钮平滑消失**
   - ✅ 起飞按钮恢复大尺寸

5. **多次测试**
   - 连续点击"起飞" 3 次
   - 观察次数增加到 3
   - 逐个点击"撤销"，验证次数递减
   - 验证次数不会低于 0

### 预期效果

- ✨ **按钮动画流畅**：撤销按钮渐入/渐出
- ✨ **点击反馈明显**：按钮点击时有缩放效果
- ✨ **数据实时更新**：状态卡片、统计数据同步更新
- ✨ **视觉语义正确**：撤销按钮使用红色（errorContainer）

---

## 🔧 查看日志（排查问题）

如果应用崩溃或行为异常：

```bash
# 查看实时日志
adb logcat | grep -E "luleme|AndroidRuntime"

# 或使用 Android Studio 的 Logcat 面板
```

---

## 📸 截图保存位置

如果您想保存截图：

```bash
# 截取屏幕
adb shell screencap -p /sdcard/screenshot.png

# 拉取到电脑
adb pull /sdcard/screenshot.png ~/Desktop/
```

---

## 🎉 快速命令参考

```bash
# 一键安装并启动
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools
cd "/Users/pin/R Migration/luleme"
./gradlew assembleDebug && \
adb install -r app/build/outputs/apk/debug/app-debug.apk && \
adb shell am start -n com.luleme/.MainActivity
```

---

## ⚡ 重新构建（修改代码后）

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
cd "/Users/pin/R Migration/luleme"

# 清理并重新构建
./gradlew clean assembleDebug

# 重新安装
export PATH=$PATH:~/Library/Android/sdk/platform-tools
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.luleme/.MainActivity
```

---

## 🎊 完成！

撤销起飞功能已完整实现并通过编译。享受您的新功能吧！✨
