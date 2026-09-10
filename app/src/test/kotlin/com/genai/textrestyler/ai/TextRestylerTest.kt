package com.genai.textrestyler.ai

import com.genai.textrestyler.data.TextStyle
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextRestylerTest {

    @Test
    fun `BUSINESS style uses correct Russian prompt modifier`() {
        val style = TextStyle.BUSINESS
        assertTrue(style.promptModifier.contains("деловой"))
        assertTrue(style.promptModifier.contains("бизнес-стиль"))
    }

    @Test
    fun `FORMAL style uses correct Russian prompt modifier`() {
        val style = TextStyle.FORMAL
        assertTrue(style.promptModifier.contains("строгий"))
        assertTrue(style.promptModifier.contains("официальный"))
    }

    @Test
    fun `FRIENDLY style uses correct Russian prompt modifier`() {
        val style = TextStyle.FRIENDLY
        assertTrue(style.promptModifier.contains("дружеский"))
        assertTrue(style.promptModifier.contains("неформальный"))
    }

    @Test
    fun `HUMOROUS style uses correct Russian prompt modifier`() {
        val style = TextStyle.HUMOROUS
        assertTrue(style.promptModifier.contains("шуточный"))
        assertTrue(style.promptModifier.contains("юмористический"))
    }

    @Test
    fun `all styles have unique labels`() {
        val labels = TextStyle.entries.map { it.label }
        assertTrue(labels.distinct().size == labels.size, "Style labels must be unique")
    }

    @Test
    fun `all styles have non-empty prompt modifiers`() {
        TextStyle.entries.forEach { style ->
            assertTrue(style.promptModifier.isNotBlank(), "${style.name} must have a non-empty prompt modifier")
        }
    }
}
