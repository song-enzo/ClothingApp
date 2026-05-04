// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 所有的插件 ID 必须放在同一个 plugins 块内
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.16" apply false
}

// 修复过时的 buildDir 警告，使用 layout.buildDirectory 代替
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
