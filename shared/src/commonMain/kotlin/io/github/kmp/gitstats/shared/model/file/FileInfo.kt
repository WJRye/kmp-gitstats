package io.github.kmp.gitstats.shared.model.file

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.math.ln
import kotlin.math.pow

@Serializable
data class FileInfo(
    val fileYearSizeStats: List<FileYearSize> = emptyList(),
    val fileTypeStats: List<FileTypeStat> = emptyList()
) {

    val totalCount: Int
        get() = fileTypeStats.sumOf { it.count }

    var totalSizeReadable: String = ""
        internal set

}

/**
 * Converts a file size in bytes into a readable string using binary units (e.g., KB, MB, GB).
 *
 * @param bytes The file size in bytes.
 * @return A string representation of the size in a more readable format, like "1.5 MB".
 */
fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val exp = (ln(bytes.toDouble()) / ln(1024.0)).toInt()
    val unit = "KMGTPE"[exp - 1]
    val size = bytes / 1024.0.pow(exp.toDouble())
    return String.format("%.1f %sB", size, unit)
}

const val NO_EXT = "no_ext"

@Serializable
data class FileTypeStat(
    val extension: String, val count: Int, val totalSizeInBytes: Long
) {

    fun ext(): String = if (extension == "no_ext") "(no extension)" else ".${extension}"
    var countPercent: String = ""
        internal set

    var totalSizePercent: String = ""
        internal set


    val countReadable: String
        get() = "$count $countPercent"

    val totalSizeReadable: String
        get() = "${formatSize(totalSizeInBytes)} $totalSizePercent"
}


@Serializable
data class FileYearSize(
    val year: LocalDate, val sizeInBytes: Long
)