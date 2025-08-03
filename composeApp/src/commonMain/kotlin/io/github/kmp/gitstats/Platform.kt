package io.github.kmp.gitstats

import io.github.kmp.gitstats.platform.DirectoryPicker
import io.github.kmp.gitstats.shared.Platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun getDirectoryPicker(): DirectoryPicker