package io.github.kmp.gitstats.shared

import java.util.Locale

class JvmLocaleLanguage : LocalLanguage {
    override fun isEnglish(): Boolean {
        return Locale.getDefault().language == "en"
    }
}
