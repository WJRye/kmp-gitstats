package io.github.kmp.gitstats.shared

import io.github.kmp.gitstats.shared.model.UserConfig

class JvmUserConfig {
    companion object {
        fun getUserConfig(path: String): UserConfig {
            val name = getValue(path, "user.name")
            val email = getValue(path, "user.email")
            return UserConfig(name, email)
        }

        private fun getValue(path: String, key: String): String {
            val value = processed(path, GIT_CONFIG_LOCAL + key).getOrElse(
                0, { "" })
            if (value.isNotEmpty()) {
                return value
            }
            return processed(path, GIT_CONFIG + key).getOrElse(0, { "" })
        }
    }
}