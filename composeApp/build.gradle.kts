import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            kotlin.srcDirs("src/desktopMain/kotlin")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.shared)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
        }
    }
    listOf(macosArm64(), macosX64()).forEach { _ ->
        dependencies {
            commonMainImplementation(libs.skiko.macos)
        }
    }
    mingwX64 {
        dependencies {
            commonMainImplementation(libs.skiko.win)
        }
    }
}

compose.desktop {
    application {
        mainClass = project.rootProject.findProperty("MAIN_CLASS") as String
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Pkg, TargetFormat.Exe, TargetFormat.Msi)
            packageName = project.rootProject.findProperty("PACKAGE_NAME") as String
            packageVersion = project.rootProject.findProperty("PACKAGE_VERSION") as String
            macOS {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon_mac.icns"))
                bundleID = project.rootProject.findProperty("BUNDLE_ID") as String
            }
            windows {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon_win.ico"))
                menuGroup = project.rootProject.findProperty("MENU_GROUP") as String
                upgradeUuid = project.rootProject.findProperty("UPGRADE_UUID") as String
            }
            buildTypes.release.proguard {
                isEnabled.set(true) // Enable ProGuard
                obfuscate.set(true) // Enable code obfuscation (not just shrink)
                optimize.set(true)
                configurationFiles.from(project.file("proguard-rules.pro"))
            }
        }
        jvmArgs += listOf(
            "-Djava.awt.headless=false",
            "-Dapple.awt.textInputClient=false",
        )
    }
}




