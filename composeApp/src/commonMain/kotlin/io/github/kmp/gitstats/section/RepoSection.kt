package io.github.kmp.gitstats.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.DialogInfo
import io.github.kmp.gitstats.SnackbarManager
import io.github.kmp.gitstats.viewmodel.AppViewModel
import io.github.kmp.gitstats.viewmodel.RepoIntent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepoSection(
    appViewModel: AppViewModel
) {
    appViewModel.sendRepoIntent(RepoIntent.LoadIntent())
    var selectedRepo by remember { mutableStateOf("") }
    val pullResult = appViewModel.pullResult.collectAsState()
    if (pullResult.value.isNotEmpty()) {
        SnackbarManager.show(pullResult.value, SnackbarDuration.Long)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(appViewModel.repos, key = { it.hashCode() }) { repo ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (repo == selectedRepo) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp).onClick {
                    selectedRepo = repo
                    appViewModel.sendRepoIntent(RepoIntent.ClickIntent(repo))
                }) {
                    Text(
                        text = repo, style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            selectedRepo = repo
                            appViewModel.sendRepoIntent(
                                RepoIntent.PullIntent(
                                    repo, DialogInfo(
                                        show = true, title = "Pulling project...", message = repo
                                    )
                                )
                            )
                        }) {
                            Text("Pull")
                        }
                        TextButton(onClick = {
                            selectedRepo = repo
                            appViewModel.sendRepoIntent(
                                RepoIntent.AnalyzeIntent(
                                    repo, DialogInfo(
                                        show = true, title = "Analyzing project...", message = repo
                                    )
                                )
                            )
                        }) {
                            Text("Analyze")
                        }
                        TextButton(onClick = {
                            appViewModel.sendRepoIntent(RepoIntent.DeleteIntent(repo))
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}