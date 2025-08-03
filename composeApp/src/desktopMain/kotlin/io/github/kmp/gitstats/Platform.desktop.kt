package io.github.kmp.gitstats

import io.github.kmp.gitstats.platform.DesktopDirectoryPicker
import io.github.kmp.gitstats.platform.DirectoryPicker
import io.github.kmp.gitstats.shared.JvmPlatform
import io.github.kmp.gitstats.shared.Platform


class JvmPlatform : Platform {
    override val name: String = "Jvm"
}

actual fun getPlatform(): Platform = JvmPlatform()
actual fun getDirectoryPicker(): DirectoryPicker = DesktopDirectoryPicker()