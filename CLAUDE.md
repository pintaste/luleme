# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

撸了么（Luleme）是一款 Android 健康记录应用，采用纯本地存储、无网络上传的隐私设计。

## 核心架构

### 技术栈
- **语言**: Kotlin
- **UI**: Jetpack Compose (Material Design 3)
- **架构模式**: MVVM
- **依赖注入**: Hilt (Dagger)
- **数据库**: Room
- **数据持久化**: DataStore Preferences

### 包结构（Clean Architecture 分层）

```
com.luleme/
├── data/                      # 数据层
│   ├── local/
│   │   ├── database/         # Room 数据库配置
│   │   ├── dao/              # 数据访问对象
│   │   └── entity/           # 数据库实体
│   ├── repository/           # Repository 实现
│   └── encryption/           # 数据加密管理
├── domain/                    # 领域层
│   ├── model/                # 领域模型
│   └── repository/           # Repository 接口
├── di/                        # 依赖注入模块
│   ├── AppModule.kt          # 应用级依赖
│   ├── DatabaseModule.kt     # 数据库依赖
│   └── RepositoryModule.kt   # Repository 依赖
└── ui/                        # 表现层
    ├── screens/              # 屏幕级组件
    │   ├── home/            # 主页（记录操作）
    │   ├── statistics/      # 统计页面（周视图、热力图）
    │   ├── settings/        # 设置页面
    │   └── lock/            # PIN 锁屏
    ├── components/          # 可复用组件
    ├── navigation/          # 导航配置
    └── theme/               # Material3 主题
```

### 依赖注入结构

- **DatabaseModule**: 提供 Room 数据库、DAO 实例
- **RepositoryModule**: 提供 Repository 实现绑定
- **AppModule**: 提供应用级单例（Gson）

所有 ViewModel 使用 `@HiltViewModel` 注解，Repository 通过构造函数注入。

### 数据流

1. **UI → ViewModel**: Compose 调用 ViewModel 方法
2. **ViewModel → Repository**: ViewModel 通过 Repository 接口操作数据
3. **Repository → DAO**: Repository 调用 Room DAO
4. **DAO → Database**: Room 处理 SQL 操作
5. **反向流**: StateFlow/LiveData 向 UI 推送数据变化

### 关键设计模式

- **Repository Pattern**: 数据层与领域层解耦
- **Dependency Injection**: Hilt 管理所有依赖
- **Entity-Model 分离**: `RecordEntity` (数据库) ↔ `Record` (领域模型)

## 构建与开发

### 构建命令

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要签名配置）
./gradlew assembleRelease

# 清理构建
./gradlew clean
```

### 版本管理

版本信息存储在根目录 `VERSION` 文件中（格式：`主版本.次版本.补丁`），构建时自动读取：
- `versionName`: 直接使用 VERSION 文件内容
- `versionCode`: 通过 `computeVersionCode()` 计算（主版本×10000 + 次版本×100 + 补丁）

### 签名配置

Release 构建需要以下环境变量：
- `SIGNING_KEY_STORE_PATH`: Keystore 文件路径
- `SIGNING_STORE_PASSWORD`: Keystore 密码
- `SIGNING_KEY_ALIAS`: 密钥别名
- `SIGNING_KEY_PASSWORD`: 密钥密码

### CI/CD

GitHub Actions 自动构建流程（`.github/workflows/build-release.yml`）：
- 触发条件：`VERSION` 文件或 `app/` 目录变更时推送到 main 分支
- 自动创建 GitHub Release，标签格式为 `v{VERSION}`
- 构建产物上传为 Artifact

## 开发规范

### 代码原则

所有代码变更必须遵循以下原则：

1. **KISS (Keep It Simple, Stupid)**: 追求简洁，避免过度设计
2. **YAGNI (You Aren't Gonna Need It)**: 只实现当前需要的功能
3. **DRY (Don't Repeat Yourself)**: 识别并抽象重复代码
4. **SOLID**:
   - 单一职责（Single Responsibility）
   - 开闭原则（Open-Closed）
   - 里氏替换（Liskov Substitution）
   - 接口隔离（Interface Segregation）
   - 依赖倒置（Dependency Inversion）

### Compose 开发

- 使用 `@Preview` 注解添加组件预览
- 状态提升（State Hoisting）：组件接收状态和回调，不直接管理 ViewModel
- 使用 `remember` 和 `rememberSaveable` 管理组件内部状态

### Room 数据库

- 实体类使用 `@Entity` 注解，定义在 `data/local/entity/`
- DAO 使用 `@Dao` 注解，定义在 `data/local/dao/`
- 数据库配置在 `AppDatabase`，当前使用 `.fallbackToDestructiveMigration()` 处理迁移

### 依赖注入

添加新的依赖注入时：
1. 在相应的 Module 中添加 `@Provides` 方法
2. 如果是接口绑定，在 `RepositoryModule` 中使用 `@Binds`
3. 确保使用 `@Singleton` 标注单例依赖

## 开发流程

本项目采用标准的 Git Flow 工作流程，所有功能开发和 Bug 修复都需要遵循以下流程：

### 1. 创建 Issue

在开始任何开发工作前，先在 GitHub 创建 Issue：

```bash
# 在 GitHub 网页端创建 Issue
# - Bug 报告：使用 Bug 报告模板
# - 新功能：使用功能请求模板
# 记录下 Issue 编号（例如 #123）
```

**Issue 最佳实践：**
- 标题清晰简洁，说明问题或功能
- 使用合适的标签（bug, enhancement, documentation 等）
- 提供足够的上下文信息和复现步骤
- 如有相关截图，一并提供

### 2. 创建功能分支

基于 `main` 分支创建新分支：

```bash
# 拉取最新代码
git checkout main
git pull origin main

# 创建新分支（命名规范）
# Bug 修复：fix/issue-编号-简短描述
git checkout -b fix/123-login-crash

# 新功能：feature/issue-编号-简短描述
git checkout -b feature/456-dark-mode

# 性能优化：perf/issue-编号-简短描述
git checkout -b perf/789-db-query

# 重构：refactor/issue-编号-简短描述
git checkout -b refactor/101-settings-viewmodel
```

**分支命名规范：**
- `feature/` - 新功能
- `fix/` - Bug 修复
- `refactor/` - 代码重构
- `perf/` - 性能优化
- `docs/` - 文档更新
- `test/` - 测试相关

### 3. 开发与提交

在功能分支上进行开发：

```bash
# 进行代码修改

# 本地测试
./gradlew clean assembleDebug
./gradlew lintDebug

# 在真机或模拟器上测试

# 提交代码（遵循 Conventional Commits 规范）
git add .
git commit -m "feat: 添加深色模式支持

- 实现主题切换功能
- 添加深色主题颜色定义
- 更新设置页面 UI

Closes #456"
```

**提交信息规范：**
- `feat:` - 新功能
- `fix:` - Bug 修复
- `refactor:` - 重构
- `perf:` - 性能优化
- `docs:` - 文档更新
- `style:` - 代码格式调整
- `test:` - 测试相关
- `chore:` - 构建/工具链相关

提交信息结构：
```
<类型>: <简短描述>

<详细描述>

Closes #<issue编号>
```

### 4. 推送分支并创建 Pull Request

```bash
# 推送分支到远程
git push origin feature/456-dark-mode

# 在 GitHub 网页端创建 Pull Request
# - 标题：简洁说明本次 PR 的目的
# - 描述：使用 PR 模板填写详细信息
# - 关联 Issue：在描述中添加 "Closes #456"
# - 指定 Reviewer（如有协作者）
```

**PR 最佳实践：**
- 确保 PR 只解决一个问题或实现一个功能
- PR 描述详细，包含测试情况和截图
- 填写 PR 模板中的所有检查清单
- 确保 CI 检查通过（PR Check workflow）
- 代码变更保持精简，避免无关修改

### 5. Code Review 与合并

```bash
# 如果 Reviewer 提出修改建议
# 在本地进行修改
git add .
git commit -m "refactor: 根据 review 意见调整代码结构"
git push origin feature/456-dark-mode

# PR 审核通过后，在 GitHub 网页端进行合并
# - 选择 "Squash and merge" 或 "Merge pull request"
# - 删除远程分支（GitHub 会提示）

# 本地清理
git checkout main
git pull origin main
git branch -d feature/456-dark-mode
```

### 6. 发布新版本

功能合并到 `main` 后，准备发布新版本：

```bash
# 1. 更新版本号
echo "1.0.2" > VERSION

# 2. 提交版本号变更
git add VERSION
git commit -m "chore: bump version to 1.0.2"
git push origin main

# 3. 创建并推送标签（触发 Release workflow）
git tag v1.0.2
git push origin v1.0.2
```

**版本发布流程：**
1. 推送标签后，GitHub Actions 自动触发 Release workflow
2. 自动构建签名的 Release APK
3. 自动创建 GitHub Release（标签为 `v1.0.2`）
4. Release 页面包含 APK 下载链接和自动生成的更新日志
5. 用户可在 Releases 页面下载最新版本

**版本号规范（语义化版本）：**
- `主版本.次版本.补丁` (例如: `1.2.3`)
- 主版本：重大架构变更或不兼容的 API 修改
- 次版本：新增功能，向后兼容
- 补丁：Bug 修复和小改进

### CI/CD 工作流

**PR Check (`.github/workflows/pr-check.yml`)**
- 触发：创建 PR 到 `main` 分支
- 流程：
  1. 构建 Debug APK
  2. 运行 Lint 检查
  3. 上传构建产物到 Artifacts
  4. 在 PR 中自动评论构建结果

**Build and Release (`.github/workflows/build-release.yml`)**
- 触发：推送 `v*` 格式的标签
- 流程：
  1. 解码签名密钥（使用 GitHub Secrets）
  2. 构建 Release APK
  3. 创建 GitHub Release
  4. 上传 APK 到 Release 页面
  5. 自动生成更新日志

## 常见任务

### 添加新屏幕

1. 在 `ui/screens/` 创建新包（如 `newfeature/`）
2. 创建 `NewFeatureScreen.kt` 和 `NewFeatureViewModel.kt`
3. 在 `ui/navigation/Screen.kt` 添加路由定义
4. 在 `ui/navigation/NavGraph.kt` 添加导航逻辑
5. ViewModel 使用 `@HiltViewModel` 注解

### 添加新数据实体

1. 在 `data/local/entity/` 创建 Entity 类（带 `@Entity` 注解）
2. 在 `domain/model/` 创建对应的 Domain Model
3. 在 `data/local/dao/` 创建 DAO 接口（带 `@Dao` 注解）
4. 在 `AppDatabase` 中注册新 Entity 和 DAO
5. 在 `DatabaseModule` 中提供 DAO 实例
6. 更新 `AppDatabase` 版本号

### 添加新 Repository

1. 在 `domain/repository/` 定义接口
2. 在 `data/repository/` 实现接口
3. 在 `RepositoryModule` 中使用 `@Binds` 绑定
4. 在 ViewModel 构造函数中注入使用

## 项目特性

### 数据安全
- 所有数据存储在 Room 本地数据库（`luleme_db`）
- 支持数据加密（`EncryptionManager`）
- 无网络权限，完全离线运行

### UI 特性
- Material Design 3 动态主题
- PIN 码锁屏功能
- 周视图和月度热力图统计
- 流畅的 Compose 动画

### 数据备份
- 支持导入/导出数据（通过 Settings 页面）
- 使用 Gson 序列化数据
