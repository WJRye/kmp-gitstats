package io.github.kmp.gitstats.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun TopNInputBox(
    topNText: String, onTopNTextChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp).width(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Top", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(6.dp))
            val underlineColor = MaterialTheme.colorScheme.outline
            Box(
                modifier = Modifier.height(IntrinsicSize.Min).drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height
                    drawLine(
                        color = underlineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }) {
                BasicTextField(
                    value = topNText,
                    onValueChange = {
                        if (it.isNotEmpty() && it.all { c -> c.isDigit() }) {
                            onTopNTextChange(it)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}