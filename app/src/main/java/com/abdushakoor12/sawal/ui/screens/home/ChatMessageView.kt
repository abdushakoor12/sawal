package com.abdushakoor12.sawal.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdushakoor12.sawal.database.ChatMessageEntity
import com.abdushakoor12.sawal.ui.theme.SawalTheme

@Composable
fun ChatMessageView(message: ChatMessageEntity) {
    val isUserMessage = message.role == "user"
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Text(
            message.message,
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