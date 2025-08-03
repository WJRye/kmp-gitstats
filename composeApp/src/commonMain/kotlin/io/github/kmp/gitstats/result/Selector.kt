package io.github.kmp.gitstats.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowDropDown
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
inline fun <reified T> Selector(
    entries: List<T>, noinline labelProvider: (T) -> String, noinline onSelect: (T) -> Unit
) {
    var selected by remember { mutableStateOf(entries.first()) }
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentWidth()) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
        ) {
            Text(
                labelProvider(selected),
                modifier = Modifier.padding(end = 2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Sharp.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.wrapContentSize()
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).wrapContentSize()
        ) {
            entries.forEach { entry ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    selected = entry
                    onSelect.invoke(entry)
                }, text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = labelProvider(entry),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f).padding(end = 2.dp)
                        )
                        if (entry == selected) {
                            Icon(
                                imageVector = Icons.TwoTone.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                })
            }
        }
    }
}