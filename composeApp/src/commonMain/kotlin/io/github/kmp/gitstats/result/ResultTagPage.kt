import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kmp.gitstats.DateUtil.Companion.formatToString
import io.github.kmp.gitstats.model.tag.TagInfo
import io.github.kmp.gitstats.result.TableCell
import io.github.kmp.gitstats.result.TableHeader

@Composable
fun ShowTag(tagInfo: TagInfo) {
    if (tagInfo.stats.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Tag Information Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Total Tags: ${tagInfo.stats.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TableHeader("Name", Modifier.weight(0.5f))
                    TableHeader("Date", Modifier.weight(1f))
                    TableHeader("Tagger", Modifier.weight(1f))
                    TableHeader("Sha", Modifier.weight(2.1f))
                    TableHeader("Message", Modifier.weight(1.5f))
                }
            }
            items(tagInfo.stats) { stat ->
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(stat.name, Modifier.weight(0.5f))
                    TableCell(
                        stat.date.formatToString(), Modifier.weight(1f)
                    )
                    TableCell(
                        stat.tagger, Modifier.weight(1f)
                    )
                    TableCell(
                        stat.sha, Modifier.weight(2.1f)
                    )
                    TableCell(stat.message, Modifier.weight(1.5f))
                }
            }
        }

    }
}