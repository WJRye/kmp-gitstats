package io.github.kmp.gitstats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kmp.gitstats.config.BuildConfig
import io.github.kmp.gitstats.section.AnalysisResultSection
import io.github.kmp.gitstats.section.RepoSection
import io.github.kmp.gitstats.viewmodel.AppViewModel
import io.github.kmp.gitstats.viewmodel.RepoIntent
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    val appViewModel = remember { AppViewModel() }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text(
                text = BuildConfig.PACKAGE_NAME,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color.LightGray)

            Main(appViewModel)
        }
        LaunchDialog(appViewModel)
        LaunchToast()
    }
}

@Composable
private fun LaunchDialog(appViewModel: AppViewModel) {
    val loadingDialogState = appViewModel.loadingDialog.collectAsState()
    if (loadingDialogState.value.show) {
        showDialog(loadingDialogState.value.title, loadingDialogState.value.message)
    }
}


@Composable
private fun LaunchToast() {
    val snackbarHostState = remember { SnackbarHostState() }
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    LaunchedEffect(Unit) {
        SnackbarManager.init(snackbarHostState)
    }
}

@Composable
private fun showDialog(title: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = false) {}) {
        Card(
            modifier = Modifier.align(Alignment.Center).wrapContentHeight().width(480.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Main(appViewModel: AppViewModel) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Workspace",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Button(
                onClick = {
                    appViewModel.sendRepoIntent(RepoIntent.AddIntent())
                }, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Import Project")
            }

            HorizontalDivider(color = Color.LightGray)

            RepoSection(appViewModel)
        }

        VerticalDivider(
            modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.LightGray
        )

        AnalysisResultSection(
            Modifier.weight(2f).padding(start = 16.dp), appViewModel
        )
    }
}



