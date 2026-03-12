#!/bin/bash

# 撸了么 - 一键安装并运行脚本

set -e  # 遇到错误立即退出

echo "🚀 撸了么 - 安装并运行"
echo "================================"

# 设置环境变量
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools

# 检查 ADB
if ! command -v adb &> /dev/null; then
    echo "❌ 错误: 找不到 adb 命令"
    echo "请确保 Android SDK platform-tools 已安装"
    exit 1
fi

# 检查设备
echo ""
echo "📱 检查连接的设备..."
DEVICE_STATUS=$(adb devices | grep -v "List" | grep -v "^$" | awk '{print $2}')

if [ -z "$DEVICE_STATUS" ]; then
    echo "❌ 错误: 未检测到设备"
    echo "请确保：1. 手机已通过 USB 连接到电脑"
    echo "        2. 手机已开启 USB 调试"
    exit 1
fi

if [ "$DEVICE_STATUS" == "unauthorized" ]; then
    echo "⚠️  设备未授权"
    echo "请在手机上点击'允许 USB 调试'对话框"
    echo "等待授权中..."
    sleep 5

    DEVICE_STATUS=$(adb devices | grep -v "List" | grep -v "^$" | awk '{print $2}')
    if [ "$DEVICE_STATUS" == "unauthorized" ]; then
        echo "❌ 设备仍未授权，请手动授权后重新运行此脚本"
        exit 1
    fi
fi

echo "✅ 设备已连接: $(adb devices | grep -v "List" | grep -v "^$" | awk '{print $1}')"

# 检查 APK 是否存在
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
    echo ""
    echo "⚠️  APK 不存在，开始构建..."
    ./gradlew assembleDebug
fi

# 卸载旧版本（避免签名冲突）
echo ""
echo "🗑️  卸载旧版本应用（如果存在）..."
adb uninstall com.luleme 2>/dev/null || echo "   (未发现旧版本)"

# 安装 APK
echo ""
echo "📦 安装应用到设备..."
adb install "$APK_PATH"

if [ $? -ne 0 ]; then
    echo "❌ 安装失败"
    exit 1
fi

echo "✅ 安装成功"

# 启动应用
echo ""
echo "🚀 启动应用..."
adb shell am start -n com.luleme/.MainActivity

if [ $? -ne 0 ]; then
    echo "❌ 启动失败"
    exit 1
fi

echo ""
echo "================================"
echo "✅ 应用已成功启动！"
echo ""
echo "🎯 测试撤销起飞功能："
echo "1. 点击'起飞'按钮"
echo "2. 观察左侧出现红色'撤销'按钮"
echo "3. 点击'撤销'按钮"
echo "4. 验证起飞次数减 1"
echo ""
echo "📊 查看日志："
echo "adb logcat | grep -E 'luleme|AndroidRuntime'"
echo ""
