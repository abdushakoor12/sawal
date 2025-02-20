package com.abdushakoor12.sawal.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.database.ChatEntity
import com.abdushakoor12.sawal.database.ChatEntityDao
import com.abdushakoor12.sawal.utils.ext.timeAgo

@Composable
fun HomeDrawerSheet(
    onChatSelected: (ChatEntity) -> Unit = {},
    onNewChatSelected: () -> Unit = {},
) {
    val chatEntityDao = rememberLookup<ChatEntityDao>()

    val chats by chatEntityDao.getAllChatsFlow().collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(chats) { chat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChatSelected(chat)
                        }
                        .padding(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Text(
                            chat.createdAt.timeAgo(),
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End),
                            lineHeight = 10.sp
                        )

                        Text(chat.title)
                    }
                }
            }
        }

        NavigationDrawerItem(
            label = { Text("New Chat") },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            onClick = { onNewChatSelected() },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            ),
            selected = false,
        )
    }
}