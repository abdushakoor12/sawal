package com.abdushakoor12.sawal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListTile(
    title: String,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    dense: Boolean = false
) {
    val textSize = if (dense) 14.sp else 16.sp
    val padding = if (dense) 8.dp else 16.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null, onClick = { onClick?.invoke() })
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                leading()
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, fontSize = textSize)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = textSize * 0.85f, color = Color.Gray)
            }
        }

        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                trailing()
            }
        }
    }
}

@Composable
fun SwitchListTile(
    title: String,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    dense: Boolean = false
) {
    val textSize = if (dense) 14.sp else 16.sp
    val padding = if (dense) 8.dp else 16.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = { onCheckedChange(!checked) })
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                leading()
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, fontSize = textSize)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = textSize * 0.85f, color = Color.Gray)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ListTilePreview() {
    Column {
        ListTile(
            title = "Regular ListTile",
            subtitle = "With subtitle",
            leading = {
                Icon(Icons.Default.Star, contentDescription = "Star Icon")
            },
            trailing = {
                Icon(Icons.Default.ArrowForward, contentDescription = "Forward Arrow")
            },
            onClick = {
                println("Clicked!")
            },
            dense = true
        )

        var switchState by remember { mutableStateOf(false) }
        SwitchListTile(
            title = "Switch ListTile",
            subtitle = "Toggle me",
            leading = {
                Icon(Icons.Default.Star, contentDescription = "Star Icon")
            },
            checked = switchState,
            onCheckedChange = { switchState = it },
            dense = true
        )
    }
}

