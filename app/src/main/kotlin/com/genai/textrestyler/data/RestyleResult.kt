package com.genai.textrestyler.data

sealed class RestyleResult {
    data object Idle : RestyleResult()
    data object Loading : RestyleResult()
    data class Success(val text: String) : RestyleResult()
    data class Error(val message: String) : RestyleResult()
}
