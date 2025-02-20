package com.abdushakoor12.sawal.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.ui.icons.ContentCopyIcon
import com.abdushakoor12.sawal.ui.icons.ShareIcon
import com.abdushakoor12.sawal.ui.theme.SawalTheme
import com.abdushakoor12.sawal.utils.ext.copyTextToClipboard
import com.abdushakoor12.sawal.utils.ext.shareText

@Composable
fun ChatMessageView(
    message: ChatMessageEntity,
    dummy: Boolean = false,
    onToggleFav: (ChatMessageEntity) -> Unit = {},
) {

    var menuOpened by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val isUserMessage = message.role == "user"
    Row(
        modifier = Modifier
            .clickable {
                menuOpened = true
            }
            .fillMaxWidth()
            .padding(
                end = if (isUserMessage) 0.dp else 12.dp,
                start = if (isUserMessage) 12.dp else 0.dp,
            ),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Text(
            if (dummy) {
                "Loading..."
            } else {
                message.message
            },
            modifier = Modifier
                .padding(2.dp)
                .background(
                    if (isUserMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp),
            color = if (isUserMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
            textAlign = if (message.role == "user") TextAlign.End else TextAlign.Start
        )

        if (!dummy) {
            DropdownMenu(
                expanded = menuOpened,
                onDismissRequest = { menuOpened = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Copy")
                    },
                    leadingIcon = {
                        Icon(ContentCopyIcon, contentDescription = "Copy")
                    },
                    onClick = {
                        context.copyTextToClipboard(message.message)
                        menuOpened = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Share")
                    },
                    leadingIcon = {
                        Icon(ShareIcon, contentDescription = "Share")
                    },
                    onClick = {
                        context.shareText(message.message)
                        menuOpened = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            if (message.fav) "Unfavorite" else "Favorite"
                        )
                    },
                    leadingIcon = {
                        Icon(
                            if (message.fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (message.fav) "Unfavorite" else "Favorite"
                        )
                    },
                    onClick = {
                        onToggleFav(message)
                        menuOpened = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatMessageViewPreview() {
    SawalTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ChatMessageView(
                ChatMessageEntity(
                    chatId = "123",
                    message = "Hello World",
                    role = "user"
                )
            )

            ChatMessageView(
                ChatMessageEntity(
                    chatId = "123",
                    message = "Hi? How are you? How may I help you?",
                    role = "assistant"
                )
            )
        }

    }
}