package io.github.kmp.gitstats

import io.github.kmp.gitstats.platform.DirectoryPicker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun getDirectoryPicker(): DirectoryPicker