package com.dark.neuroverse.compose.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DownloadForOffline
import androidx.compose.material.icons.twotone.FreeBreakfast
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    height: Dp = 50.dp,
    onSelected: (index: Int, label: String) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Installed", "Market")

    SingleChoiceSegmentedButtonRow(modifier) {
        options.forEachIndexed { index, label ->

            val cornerRadius = 12.dp // Less rounded
            val shape = when (index) {
                0 -> RoundedCornerShape(
                    topStart = cornerRadius,
                    bottomStart = cornerRadius,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )

                options.size - 1 -> RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = cornerRadius,
                    bottomEnd = cornerRadius
                )

                else -> RoundedCornerShape(0.dp)
            }


            SegmentedButton(
                modifier = Modifier.height(height),
                shape = shape,
                onClick = {
                    selectedIndex = index
                    onSelected(index, label)
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                selected = index == selectedIndex,
                icon = {
                    Icon(
                        imageVector = if (index == 1) Icons.TwoTone.DownloadForOffline else Icons.TwoTone.FreeBreakfast,
                        contentDescription = label
                    )
                },
                label = { Text(label) }
            )
        }
    }
}