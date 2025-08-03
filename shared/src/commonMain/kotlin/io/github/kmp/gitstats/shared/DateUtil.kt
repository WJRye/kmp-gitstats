package io.github.kmp.gitstats.shared

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

class DateUtil {
    companion object {

        @JvmStatic
        @OptIn(ExperimentalTime::class)
        fun currentMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

        @JvmStatic
        @OptIn(ExperimentalTime::class)
        fun localDateTime(): LocalDateTime =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        @JvmStatic
        fun formatToDuration(millis: Long): String {
            val seconds = millis / 1000
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60
            return if (hours == 0L) "%02d:%02d".format(minutes, remainingSeconds)
            else "%02d:%02d:%02d".format(hours, minutes, remainingSeconds)
        }

        @JvmStatic
        fun LocalDateTime.formatToString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
            val javaTime = java.time.LocalDateTime.of(year, month.number, day, hour, minute, second)
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return javaTime.format(formatter)
        }


        @JvmStatic
        fun LocalDate.formatToString(pattern: String = "yyyy-MM-dd"): String {
            val javaTime = java.time.LocalDateTime.of(year, month.number, day, 0, 0, 0)
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return javaTime.format(formatter)
        }

        @JvmStatic
        fun LocalDate.formatToMDString(): String {
            return formatToString("MM-dd")
        }

        @JvmStatic
        fun LocalDate.formatToYString(): String {
            return formatToString("yyyy")
        }

        @JvmStatic
        @OptIn(/**/ExperimentalTime::class)
        fun isoDateToLocalDateTime(dateStr: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
            val zonedDateTime = ZonedDateTime.parse(dateStr, formatter)
            val instant = zonedDateTime.toInstant()
            return instant.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }
}