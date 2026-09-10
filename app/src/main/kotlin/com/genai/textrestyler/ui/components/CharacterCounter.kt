package com.genai.textrestyler.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CharacterCounter(
    currentLength: Int,
    maxLength: Int,
    modifier: Modifier = Modifier
) {
    val color = if (currentLength >= maxLength) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = "$currentLength / $maxLength",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}
