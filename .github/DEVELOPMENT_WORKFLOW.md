# 开发流程快速参考

## 🚀 快速开始

```bash
# 1. 创建 Issue (在 GitHub 网页端)
# 2. 创建分支
git checkout main && git pull
git checkout -b feature/123-your-feature

# 3. 开发并测试
./gradlew clean assembleDebug
# 在设备上测试

# 4. 提交代码
git add .
git commit -m "feat: 你的功能描述

详细说明

Closes #123"

# 5. 推送并创建 PR
git push origin feature/123-your-feature
# 在 GitHub 创建 PR

# 6. 合并后发布
echo "1.0.2" > VERSION
git add VERSION
git commit -m "chore: bump version to 1.0.2"
git push origin main
git tag v1.0.2
git push origin v1.0.2
```

## 📋 分支命名规范

| 类型 | 前缀 | 示例 |
|------|------|------|
| 新功能 | `feature/` | `feature/123-dark-mode` |
| Bug 修复 | `fix/` | `fix/456-crash-on-startup` |
| 重构 | `refactor/` | `refactor/789-viewmodel` |
| 性能优化 | `perf/` | `perf/101-db-query` |
| 文档 | `docs/` | `docs/202-readme-update` |
| 测试 | `test/` | `test/303-unit-tests` |

## 💬 提交信息类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat:` | 新功能 | `feat: 添加深色模式` |
| `fix:` | Bug 修复 | `fix: 修复启动崩溃问题` |
| `refactor:` | 重构 | `refactor: 重构 ViewModel 层` |
| `perf:` | 性能优化 | `perf: 优化数据库查询` |
| `docs:` | 文档更新 | `docs: 更新 README` |
| `style:` | 代码格式 | `style: 格式化代码` |
| `test:` | 测试 | `test: 添加单元测试` |
| `chore:` | 构建/工具 | `chore: 更新依赖版本` |

## 🔄 完整流程图

```
┌─────────────┐
│  创建 Issue  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  创建分支    │  git checkout -b feature/xxx
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  开发 & 测试 │  ./gradlew assembleDebug
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  提交代码    │  git commit -m "feat: ..."
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  创建 PR     │  git push + GitHub PR
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  CI 检查     │  自动运行构建和 Lint
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Code Review  │  Reviewer 审核代码
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  合并到 main │  Squash and merge
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  更新版本号  │  echo "1.0.2" > VERSION
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  创建标签    │  git tag v1.0.2
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 自动发布 APK │  GitHub Actions Release
└─────────────┘
```

## 🛠️ 常用命令

### 构建命令
```bash
# Debug 构建
./gradlew assembleDebug

# Release 构建
./gradlew assembleRelease

# 清理构建
./gradlew clean

# 运行 Lint
./gradlew lintDebug
```

### Git 命令
```bash
# 查看状态
git status

# 查看分支
git branch -a

# 同步主分支
git checkout main
git pull origin main

# 删除本地分支
git branch -d feature/xxx

# 删除远程分支
git push origin --delete feature/xxx

# 查看标签
git tag -l

# 删除标签
git tag -d v1.0.0
git push origin --delete v1.0.0
```

## ⚙️ CI/CD 触发条件

| Workflow | 触发条件 | 作用 |
|----------|---------|------|
| PR Check | PR 到 main 分支 | 构建 Debug APK，运行 Lint |
| Build and Release | 推送 `v*` 标签 | 构建 Release APK，创建 GitHub Release |

## 📝 注意事项

- ✅ 始终从 Issue 开始开发
- ✅ 分支名称包含 Issue 编号
- ✅ 提交信息遵循 Conventional Commits
- ✅ PR 描述使用模板并填写完整
- ✅ 合并前确保 CI 检查通过
- ✅ 发布前更新 VERSION 文件
- ✅ 使用标签触发正式发布

## 🔐 Release 签名配置

Release 构建需要在 GitHub Secrets 中配置：

- `SIGNING_KEY_BASE64`: Base64 编码的 Keystore 文件
- `KEY_ALIAS`: 密钥别名
- `KEY_PASSWORD`: 密钥密码
- `KEY_STORE_PASSWORD`: Keystore 密码

```bash
# 生成 Base64 编码的 Keystore
base64 -i your-keystore.jks | pbcopy
```
