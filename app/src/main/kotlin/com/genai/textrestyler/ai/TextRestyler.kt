package com.genai.textrestyler.ai

import com.genai.textrestyler.data.RestyleResult
import com.genai.textrestyler.data.TextStyle
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.SystemInstruction
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextRestyler @Inject constructor(
    private val generativeModel: GenerativeModel
) {

    companion object {
        private const val SYSTEM_INSTRUCTION =
            "Ты — текстовый редактор. Твоя задача — переписать текст пользователя " +
            "в указанном стиле. Сохрани смысл и основные факты. Верни ТОЛЬКО " +
            "переписанный текст, без пояснений и комментариев."
    }

    suspend fun restyle(inputText: String, style: TextStyle): RestyleResult {
        return try {
            val userPrompt = buildString {
                appendLine("Стиль: ${style.promptModifier}")
                appendLine()
                appendLine("Текст:")
                append(inputText)
            }

            val request = generateContentRequest(TextPart(userPrompt)) {
                this.systemInstruction = SystemInstruction(SYSTEM_INSTRUCTION)
                this.temperature = 0.7f
                this.topK = 40
                this.maxOutputTokens = 1024
            }

            val response = generativeModel.generateContent(request)
            val responseText = response.candidates.firstOrNull()?.text.orEmpty()
            val resultText = sanitizeOutput(responseText)

            if (resultText.isBlank()) {
                RestyleResult.Error("Не удалось получить результат. Попробуйте изменить текст.")
            } else {
                RestyleResult.Success(resultText)
            }
        } catch (e: Exception) {
            RestyleResult.Error(
                "Ошибка обработки: ${e.localizedMessage ?: "Неизвестная ошибка. Попробуйте позже."}"
            )
        }
    }

    private fun sanitizeOutput(text: String): String {
        return text
            .replace(Regex("```[a-zA-Z]*\\n?"), "")
            .replace("```", "")
            .trim()
    }
}
