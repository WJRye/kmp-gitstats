package io.github.kmp.gitstats.platform

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

class DesktopDirectoryPicker : DirectoryPicker {
    override fun chooseDirectory(): List<String> {
        val chooser = JFileChooser(FileSystemView.getFileSystemView())
        chooser.dialogTitle = "Select Project Directory"
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.isAcceptAllFileFilterUsed = false
        chooser.isMultiSelectionEnabled = true
        val result = chooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFiles.asSequence().filter { containsGit(it.path) }.map { it.path }
                .toList()
        } else emptyList()
    }

    private fun containsGit(path: String): Boolean {
        return File(path, ".git").exists()
    }
}