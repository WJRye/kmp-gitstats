package io.github.kmp.gitstats.section

import ShowTag
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.result.ResultTab
import io.github.kmp.gitstats.result.ShowActivity
import io.github.kmp.gitstats.result.ShowAuthor
import io.github.kmp.gitstats.result.ShowFile
import io.github.kmp.gitstats.result.ShowLine
import io.github.kmp.gitstats.result.ShowOverview
import io.github.kmp.gitstats.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnalysisResultSection(
    modifier: Modifier,
    appViewModel: AppViewModel,
) {
    val analysisResult = appViewModel.analysisResult.collectAsState()
    var selectedTab by remember { mutableStateOf(ResultTab.Overview) }
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = analysisResult.value.overViewInfo.projectName.ifEmpty { "Analysis Result" },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        val tabs = ResultTab.entries.toTypedArray()
        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxWidth()) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                for (tab in tabs) {
                    Tab(selected = selectedTab == tab, onClick = {
                        selectedTab = tab
                        scope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    }) {
                        Text(tab.name, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f) // Take remaining height
            ) { page ->
                // Display content based on the selected tab (page index)
                Surface(
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        when (page) {
                            ResultTab.Overview.ordinal -> {
                                ShowOverview(analysisResult.value.overViewInfo)
                            }

                            ResultTab.Activity.ordinal -> {
                                ShowActivity(analysisResult.value.activityInfo)
                            }

                            ResultTab.Authors.ordinal -> {
                                ShowAuthor(analysisResult.value.authorInfo)
                            }

                            ResultTab.Files.ordinal -> {
                                ShowFile(analysisResult.value.fileInfo)
                            }

                            ResultTab.Lines.ordinal -> {
                                ShowLine(analysisResult.value.lineInfo)
                            }

                            ResultTab.Tags.ordinal -> {
                                ShowTag(analysisResult.value.tagInfo)
                            }

                        }
                    }
                }
            }
        }
    }
}