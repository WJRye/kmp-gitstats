package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.AnalysisResult

interface GitAnalyzer {

    fun analyze(): AnalysisResult

    fun pull(): Pair<Int, String>
}