# 服装产品管理 Android App

一个用 Kotlin + Jetpack Compose 构建的原生 Android 应用，支持服装产品管理、成本计算和本地照片存储。

## 功能特性

- **产品管理**：添加、编辑、删除服装产品
- **成本计算**：自动计算面料、工价、烫和纽扣、辅料等成本
- **搜索功能**：按编号、名称、工厂快速搜索
- **详情展示**：查看产品详细信息和成本明细
- **本地存储**：使用 SQLite 数据库存储所有数据
- **照片管理**：支持添加产品图片链接

## 快速开始

### 使用 GitHub Actions 编译（推荐）

1. **上传到 GitHub**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/ClothingApp.git
   git push -u origin main
   ```

2. **自动编译**
   - 推送代码后，GitHub Actions 会自动编译 APK
   - 在 Actions 标签页查看编译进度
   - 编译完成后，在 Artifacts 中下载 APK 文件

3. **安装到手机**
   - 将 APK 文件传到手机
   - 打开文件管理器，点击 APK 文件安装
   - 或使用 ADB 命令：`adb install app-release.apk`

### 本地编译（需要 Android Studio）

1. **安装 Android Studio**
   - 下载：https://developer.android.com/studio

2. **打开项目**
   - 在 Android Studio 中打开此项目

3. **编译和运行**
   - 连接 Android 手机（启用 USB 调试）
   - 点击 Run 按钮
   - 应用会自动安装到手机

## 项目结构

```
ClothingApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/clothingapp/
│   │       │   ├── MainActivity.kt          # 主 Activity
│   │       │   ├── data/                    # 数据层
│   │       │   │   ├── Product.kt           # 产品数据模型
│   │       │   │   ├── ProductDao.kt        # 数据库访问
│   │       │   │   ├── AppDatabase.kt       # 数据库配置
│   │       │   │   └── ProductRepository.kt # 数据仓库
│   │       │   └── ui/                      # UI 层
│   │       │       ├── ProductViewModel.kt  # 视图模型
│   │       │       ├── theme/               # 主题配置
│   │       │       └── screens/             # 页面
│   │       │           ├── HomeScreen.kt    # 主页
│   │       │           ├── AddProductScreen.kt
│   │       │           └── ProductDetailScreen.kt
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── .github/workflows/build-apk.yml          # GitHub Actions 配置
└── README.md
```

## 技术栈

- **语言**：Kotlin
- **UI 框架**：Jetpack Compose
- **数据库**：Room
- **导航**：Compose Navigation
- **图片加载**：Coil
- **构建工具**：Gradle

## 系统要求

- **最低 API 级别**：24 (Android 7.0)
- **目标 API 级别**：34 (Android 14)
- **最小内存**：2GB RAM

## 使用说明

### 添加新产品

1. 点击主页面右上角的 "+" 按钮
2. 填写产品信息：
   - 编号、名称、工厂名
   - 4 项面料（名称和价格）
   - 工价、烫和纽扣、辅料
3. 点击"保存"按钮

### 查看产品详情

1. 在主页面点击任意产品卡片
2. 查看产品大图、详细信息和成本明细

### 搜索产品

1. 在主页面搜索栏输入关键词
2. 支持按编号、名称、工厂名搜索

### 删除产品

1. 打开产品详情页
2. 点击右上角的删除按钮

## 常见问题

**Q: 如何获取 APK 文件？**
A: 推送代码到 GitHub 后，GitHub Actions 会自动编译。在 Actions 标签页的 Artifacts 中下载编译好的 APK。

**Q: 手机上的数据会丢失吗？**
A: 不会。所有数据都存储在手机的本地数据库中，只要不卸载应用就不会丢失。

**Q: 可以在多部手机上使用吗？**
A: 可以。每部手机都有独立的数据库，各自保存各自的数据。

**Q: 如何备份数据？**
A: 可以通过 Android 的备份功能备份应用数据，或者导出数据库文件。

## 许可证

MIT License

## 联系方式

有任何问题或建议，欢迎提交 Issue 或 Pull Request。
