package io.github.kmp.gitstats

import io.github.kmp.gitstats.platform.DesktopDirectoryPicker
import io.github.kmp.gitstats.platform.DirectoryPicker


class JvmPlatform : Platform {
    override val name: String = "Jvm"
}

actual fun getPlatform(): Platform = JvmPlatform()
actual fun getDirectoryPicker(): DirectoryPicker = DesktopDirectoryPicker()