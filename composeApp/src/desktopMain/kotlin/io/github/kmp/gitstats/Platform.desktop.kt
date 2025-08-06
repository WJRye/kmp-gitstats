package io.github.kmp.gitstats

import io.github.kmp.gitstats.platform.DesktopDirectoryPicker
import io.github.kmp.gitstats.platform.DirectoryPicker

actual fun getDirectoryPicker(): DirectoryPicker = DesktopDirectoryPicker()