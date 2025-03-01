package com.abdushakoor12.sawal.ui.screens.select_model

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.abdushakoor12.sawal.core.PrefManager
import com.abdushakoor12.sawal.core.rememberLookup
import com.abdushakoor12.sawal.database.AppDatabase
import com.abdushakoor12.sawal.database.OpenRouterModelEntity
import java.text.DecimalFormat

private val modelProviders = listOf(
    "Google",
    "OpenAI",
    "Anthropic",
    "Gemini",
    "xAI",
    "Mistral",
    "Microsoft",
)

enum class ModelCost { All, Free, Paid }

class SelectModelScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val prefManager = rememberLookup<PrefManager>()
        val selectedModelId by prefManager.selectedModelFlow.collectAsState(initial = null)
        val db = rememberLookup<AppDatabase>()

        var selectedProviders by remember { mutableStateOf(emptySet<String>()) }

        var modelCost by remember { mutableStateOf(ModelCost.All) }

        var availableModels by remember { mutableStateOf<List<OpenRouterModelEntity>>(emptyList()) }

        LaunchedEffect(key1 = true) {
            db.orModelEntityDao().getAllModels().let { availableModels = it }
        }

        val searchText = remember { mutableStateOf("") }
        val filteredModels =
            remember(searchText.value, availableModels.size, selectedProviders, modelCost) {
                availableModels
                    .filter {
                        when (modelCost) {
                            ModelCost.All -> true
                            ModelCost.Free -> it.name.contains("(free)")
                            ModelCost.Paid -> !it.name.contains("(free)")
                        }
                    }
                    .filter {
                        if (selectedProviders.isEmpty()) {
                            true
                        } else {
                            selectedProviders.contains(it.name.split(":").first())
                        }
                    }
                    .filter { it.name.contains(searchText.value, true) }
            }

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Select Model (${filteredModels.size})") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modelProviders.forEach { provider ->
                        FilterChip(
                            selected = selectedProviders.contains(provider),
                            onClick = {
                                selectedProviders = if (selectedProviders.contains(provider)) {
                                    selectedProviders - provider
                                } else {
                                    selectedProviders + provider
                                }
                            },
                            label = { Text(provider) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModelCost.entries.forEach { cost ->
                        FilterChip(
                            selected = modelCost == cost,
                            onClick = {
                                modelCost = cost
                            },
                            label = { Text(cost.name) }
                        )
                    }
                }

                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(filteredModels) { model ->
                        val selected = selectedModelId == model.id

                        ModelView(
                            selected, prefManager, model
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ModelView(
        selected: Boolean,
        prefManager: PrefManager,
        model: OpenRouterModelEntity,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable {
                    prefManager.setSelectedModel(model.id)
                }
                .padding(8.dp),
        ) {
            val pricesList = listOf(
                Pair(model.pricePerPrompt, "prompt"),
                Pair(model.pricePerCompletion, "completion"),
//                Pair(model.pricePerImage, "image"),
                Pair(model.pricePerRequest, "request"),
            ).map {
                Pair((it.first.toDoubleOrNull() ?: 0.0) * 1_000_000, it.second)
            }.filter { it.first > 0.0 }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                ) {
                    Text(
                        model.name,
                        lineHeight = 14.sp,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    )
                }
            }

            pricesList.forEach { (price, type) ->
                val formattedPrice = DecimalFormat("#.###").format(price)
                Text(
                    "$type ${formattedPrice}/M",
                    lineHeight = 8.sp,
                    fontSize = 8.sp,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}