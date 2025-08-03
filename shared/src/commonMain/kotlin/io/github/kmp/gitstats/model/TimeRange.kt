package io.github.kmp.gitstats.model

enum class TimeRange(val label: String, val days: Int) {
    WEEK("Past Week", 7), MONTH("Past Month", 30), THREE_MONTHS(
        "Past 3 Months", 90
    ),
    HALF_YEAR("Past 6 Months", 180), YEAR("Past Year", 365)
}
