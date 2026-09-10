package com.genai.textrestyler.ai

import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class ModelState {
    data object Checking : ModelState()
    data object Available : ModelState()
    data object Downloadable : ModelState()
    data class Downloading(val progress: Float) : ModelState()
    data class Unavailable(val message: String) : ModelState()
}

@Singleton
class ModelStatusChecker @Inject constructor(
    private val generativeModel: GenerativeModel
) {

    suspend fun checkStatus(): ModelState {
        return when (generativeModel.checkStatus()) {
            FeatureStatus.AVAILABLE -> ModelState.Available
            FeatureStatus.DOWNLOADABLE -> ModelState.Downloadable
            FeatureStatus.DOWNLOADING -> ModelState.Downloading(progress = 0f)
            FeatureStatus.UNAVAILABLE -> ModelState.Unavailable(
                message = "Устройство не поддерживает AI-функции"
            )
            else -> ModelState.Unavailable(
                message = "Неизвестный статус модели"
            )
        }
    }

    fun download(): Flow<DownloadStatus> {
        return generativeModel.download()
    }
}
