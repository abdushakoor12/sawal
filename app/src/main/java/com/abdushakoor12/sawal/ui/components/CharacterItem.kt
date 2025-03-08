package com.abdushakoor12.sawal.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdushakoor12.sawal.database.CharacterEntity

@Composable
fun CharacterItem(
    character: CharacterEntity,
    onDelete: (CharacterEntity) -> Unit,
    onClick: (CharacterEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ListTile(
            onClick = { onClick(character) },
            title = character.name,
            subtitle = character.description.takeIf { it.isNotBlank() }
                ?.let { if (it.length > 100) "${it.take(100)}..." else it },
            leading = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Character Icon"
                )
            },
            trailing = {
                IconButton(onClick = { onDelete(character) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Character"
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterItemPreview() {
    val character = CharacterEntity(
        name = "Example Character",
        description = "This is an example character with a description that explains their personality and traits."
    )

    CharacterItem(
        character = character,
        onDelete = {},
        onClick = {}
    )
} 