package io.github.kmp.gitstats.platform

interface DirectoryPicker {
    fun chooseDirectory(): List<String>
}