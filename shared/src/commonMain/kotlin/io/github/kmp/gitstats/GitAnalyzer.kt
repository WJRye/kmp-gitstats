package io.github.kmp.gitstats

import io.github.kmp.gitstats.model.AnalysisResult

interface GitAnalyzer {

    fun analyze(): AnalysisResult

    fun pull(): Pair<Int, String>
}