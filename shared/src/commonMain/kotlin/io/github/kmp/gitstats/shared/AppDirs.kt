package io.github.kmp.gitstats.shared

import  io.github.kmp.gitstats.config.BuildConfig
import java.io.File

object AppDirs {

    private const val PACKAGE_NAME = BuildConfig.PACKAGE_NAME

    /**
     * Returns the platform-specific application data directory for the given app name.
     *
     * This method determines the correct base directory based on the operating system:
     * - On Windows: Uses the APPDATA environment variable, or defaults to the user's Roaming profile.
     * - On macOS: Uses the "Library/Application Support" directory under the user's home.
     * - On Linux/Unix: Uses the XDG_DATA_HOME environment variable, or defaults to "~/.local/share".
     *
     * If the directory does not exist, it will be created.
     *
     * @return A [File] pointing to the application-specific data directory.
     */
    fun getAppDataDir(): File {
        val appName = PACKAGE_NAME
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        return when {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
                File(appData, appName)
            }

            os.contains("mac") -> File(userHome, "Library/Application Support/$appName")
            else -> {
                val xdgDataHome = System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share"
                File(xdgDataHome, appName)
            }
        }.apply {
            if (!exists()) mkdirs()
        }
    }
}
