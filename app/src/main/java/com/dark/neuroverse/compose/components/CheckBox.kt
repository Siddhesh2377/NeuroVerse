package com.dark.neuroverse.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CheckBX(checked: Boolean, isReadOnly: Boolean = false, onCheckStateChange: (Boolean) -> Unit) {

    val rounded = 8.dp

    Box(
        modifier = Modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                color = if (checked) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(rounded)
            )
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(rounded)
            )
            .clickable {
                if (!isReadOnly) onCheckStateChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                modifier = Modifier.padding(3.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

}