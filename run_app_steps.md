# 运行应用步骤

## 在IDE内运行应用（无需Android Studio）

### 1. 设置环境变量
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### 2. 构建应用
```bash
./gradlew assembleDebug
```

### 3. 检查可用设备
```bash
adb devices
```

### 4. 安装并启动应用
```bash
# 安装应用
adb -s [设备ID] install app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb -s [设备ID] shell am start -n com.luleme/.MainActivity
```

### 注意事项
- 首次构建可能需要下载依赖，时间会稍长
- 确保设备已连接并开启USB调试
- 如有多个设备，需要指定设备ID
- 每次修改代码后，运行这些步骤来测试更改
