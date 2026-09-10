package com.genai.textrestyler.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genai.textrestyler.ai.DeviceCheckResult
import com.genai.textrestyler.ai.DeviceChecker
import com.genai.textrestyler.ai.DeviceInfo
import com.genai.textrestyler.ai.ModelState
import com.genai.textrestyler.ai.ModelStatusChecker
import com.genai.textrestyler.ai.TextRestyler
import com.genai.textrestyler.data.RestyleResult
import com.genai.textrestyler.data.TextStyle
import com.google.mlkit.genai.common.DownloadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    data object ModelLoading : UiState()
    data class ModelDownloading(val progress: Float) : UiState()
    data class ModelUnavailable(val message: String) : UiState()
    data class DeviceIncompatible(val deviceInfo: DeviceInfo) : UiState()
    data class Ready(
        val inputText: String = "",
        val selectedStyle: TextStyle = TextStyle.BUSINESS,
        val result: RestyleResult = RestyleResult.Idle
    ) : UiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val textRestyler: TextRestyler,
    private val modelStatusChecker: ModelStatusChecker,
    private val deviceChecker: DeviceChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.ModelLoading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        checkDeviceCompatibility()
    }

    private fun checkDeviceCompatibility() {
        when (val result = deviceChecker.check()) {
            is DeviceCheckResult.Compatible -> {
                // Устройство совместимо — переходим к проверке модели
                checkModelStatus()
            }
            is DeviceCheckResult.Incompatible -> {
                _uiState.value = UiState.DeviceIncompatible(deviceInfo = result.info)
            }
        }
    }

    private fun checkModelStatus() {
        viewModelScope.launch {
            when (val status = modelStatusChecker.checkStatus()) {
                is ModelState.Available -> {
                    _uiState.value = UiState.Ready()
                }
                is ModelState.Downloadable -> {
                    _uiState.value = UiState.Ready()
                    // Model is downloadable but let user use the UI;
                    // download will be triggered on first inference or via banner
                }
                is ModelState.Downloading -> {
                    _uiState.value = UiState.ModelDownloading(progress = status.progress)
                }
                is ModelState.Unavailable -> {
                    _uiState.value = UiState.ModelUnavailable(message = status.message)
                }
                is ModelState.Checking -> { /* remain in ModelLoading */ }
            }
        }
    }

    fun updateInputText(text: String) {
        val state = _uiState.value
        if (state is UiState.Ready) {
            _uiState.update { state.copy(inputText = text.take(1000)) }
        }
    }

    fun selectStyle(style: TextStyle) {
        val state = _uiState.value
        if (state is UiState.Ready) {
            _uiState.update { state.copy(selectedStyle = style) }
        }
    }

    fun restyle() {
        val state = _uiState.value
        if (state !is UiState.Ready) return
        if (state.inputText.isBlank()) return
        if (state.result is RestyleResult.Loading) return

        _uiState.update { state.copy(result = RestyleResult.Loading) }

        viewModelScope.launch {
            val result = textRestyler.restyle(
                inputText = state.inputText,
                style = state.selectedStyle
            )
            val currentState = _uiState.value
            if (currentState is UiState.Ready) {
                _uiState.update { currentState.copy(result = result) }
            }
        }
    }

    fun startModelDownload() {
        viewModelScope.launch {
            _uiState.value = UiState.ModelDownloading(progress = 0f)
            var totalBytesToDownload = 0L
            modelStatusChecker.download().collect { downloadStatus ->
                when (downloadStatus) {
                    is DownloadStatus.DownloadStarted -> {
                        totalBytesToDownload = downloadStatus.bytesToDownload
                        _uiState.value = UiState.ModelDownloading(progress = 0f)
                    }
                    is DownloadStatus.DownloadProgress -> {
                        val progress = if (totalBytesToDownload > 0L) {
                            downloadStatus.totalBytesDownloaded.toFloat() / totalBytesToDownload.toFloat()
                        } else 0f
                        _uiState.value = UiState.ModelDownloading(progress = progress)
                    }
                    is DownloadStatus.DownloadCompleted -> {
                        _uiState.value = UiState.Ready()
                    }
                    is DownloadStatus.DownloadFailed -> {
                        _uiState.value = UiState.ModelUnavailable(
                            message = "Ошибка загрузки модели: ${downloadStatus.e.localizedMessage ?: "Проверьте подключение к интернету."}"
                        )
                    }
                }
            }
        }
    }
}

