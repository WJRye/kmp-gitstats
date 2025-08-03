package io.github.kmp.gitstats.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.shared.model.author.AuthorInfo
import io.github.kmp.gitstats.result.author.ContributionActivity
import io.github.kmp.gitstats.result.author.TopContributors


@Composable
fun ShowAuthor(authorInfo: AuthorInfo) {
    if (authorInfo.authorStats.isEmpty()) return
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        TopContributors(authorInfo.authorStats)
        Spacer(Modifier.height(32.dp))
        HorizontalDivider(
            thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(32.dp))
        ContributionActivity(authorInfo.authorContributionInfo)
    }

}
