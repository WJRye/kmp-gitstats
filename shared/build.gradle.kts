plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
    applyDefaultHierarchyTemplate()
    jvm()
    sourceSets {
        commonMain {
            kotlin.srcDirs("src/commonMain/kotlin")
            kotlin.srcDir(layout.buildDirectory.dir("generated/config/commonMain"))
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.serialization.json)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
    mingwX64()
    macosArm64()
    macosX64()
}

val generateBuildConfig by tasks.registering {
    group = "build"
    description = "Generates BuildConfig.kt"

    val outputDir = layout.buildDirectory.dir("generated/config/commonMain")
    outputs.dir(outputDir)

    val dir = outputDir.get().asFile
    val name = project.rootProject.findProperty("PACKAGE_NAME")
    val version = project.rootProject.findProperty("PACKAGE_VERSION")

    val configFile = dir.resolve("io/github/kmp/gitstats/config/BuildConfig.kt")
    configFile.parentFile.mkdirs()
    configFile.writeText(
        """
            package io.github.kmp.gitstats.config

            object BuildConfig {
                const val PACKAGE_NAME = "$name"
                const val PACKAGE_VERSION = "$version"
            }
            """.trimIndent()
    )
}

tasks.findByPath("compileKotlinJvm")?.dependsOn(generateBuildConfig)