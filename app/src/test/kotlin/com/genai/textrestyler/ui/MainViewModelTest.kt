package com.genai.textrestyler.ui

import com.genai.textrestyler.data.TextStyle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainViewModelTest {

    @Test
    fun `default style is BUSINESS`() {
        val defaultStyle = TextStyle.BUSINESS
        assertEquals("Деловой", defaultStyle.label)
    }

    @Test
    fun `TextStyle has exactly 4 entries`() {
        assertEquals(4, TextStyle.entries.size)
    }

    @Test
    fun `TextStyle entries are in correct order`() {
        val entries = TextStyle.entries
        assertEquals(TextStyle.BUSINESS, entries[0])
        assertEquals(TextStyle.FORMAL, entries[1])
        assertEquals(TextStyle.FRIENDLY, entries[2])
        assertEquals(TextStyle.HUMOROUS, entries[3])
    }
}
