package com.genai.textrestyler.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.genai.textrestyler.data.RestyleResult
import com.genai.textrestyler.ui.components.CharacterCounter
import com.genai.textrestyler.ui.components.DeviceIncompatibleBanner
import com.genai.textrestyler.ui.components.ModelDownloadingBanner
import com.genai.textrestyler.ui.components.ModelLoadingBanner
import com.genai.textrestyler.ui.components.ModelUnavailableBanner

private const val MAX_INPUT_LENGTH = 1000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Текстовый Рестайлер") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.DeviceIncompatible -> {
                    DeviceIncompatibleBanner(deviceInfo = state.deviceInfo)
                }

                is UiState.ModelLoading -> {
                    ModelLoadingBanner()
                }

                is UiState.ModelDownloading -> {
                    ModelDownloadingBanner(progress = state.progress)
                }

                is UiState.ModelUnavailable -> {
                    ModelUnavailableBanner(
                        message = state.message,
                        onRetry = { viewModel.startModelDownload() }
                    )
                }

                is UiState.Ready -> {
                    readyContent(
                        state = state,
                        onInputChanged = viewModel::updateInputText,
                        onStyleSelected = viewModel::selectStyle,
                        onRestyleClicked = viewModel::restyle
                    )
                }
            }
        }
    }
}

@Composable
private fun readyContent(
    state: UiState.Ready,
    onInputChanged: (String) -> Unit,
    onStyleSelected: (com.genai.textrestyler.data.TextStyle) -> Unit,
    onRestyleClicked: () -> Unit
) {
    val isLoading = state.result is RestyleResult.Loading
    val isButtonEnabled = state.inputText.isNotBlank() && !isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Input text field
        OutlinedTextField(
            value = state.inputText,
            onValueChange = { newText ->
                if (newText.length <= MAX_INPUT_LENGTH) {
                    onInputChanged(newText)
                }
            },
            label = { Text("Введите текст") },
            placeholder = { Text("Введите текст для обработки...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            maxLines = 8,
            supportingText = {
                CharacterCounter(
                    currentLength = state.inputText.length,
                    maxLength = MAX_INPUT_LENGTH
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Style selector
        StyleSelector(
            selectedStyle = state.selectedStyle,
            onStyleSelected = onStyleSelected,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Analyze button with loading indicator
        Button(
            onClick = onRestyleClicked,
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text("Обработка...")
            } else {
                Text("Анализировать")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Output field / result
        when (val result = state.result) {
            is RestyleResult.Idle -> {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Результат") },
                    placeholder = { Text("Здесь появится результат...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }

            is RestyleResult.Loading -> {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Результат") },
                    placeholder = { Text("Обработка текста...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }

            is RestyleResult.Success -> {
                OutlinedTextField(
                    value = result.text,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Результат") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
            }

            is RestyleResult.Error -> {
                OutlinedTextField(
                    value = result.message,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ошибка") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.error
                    ),
                    isError = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
