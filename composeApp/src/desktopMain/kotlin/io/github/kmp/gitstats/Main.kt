package io.github.kmp.gitstats

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.kmp.gitstats.config.BuildConfig
import kmp_gitstats.composeapp.generated.resources.Res
import kmp_gitstats.composeapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "${BuildConfig.PACKAGE_NAME} v${BuildConfig.PACKAGE_VERSION}",
        icon = painterResource(Res.drawable.icon)
    ) {
        App()
    }
}