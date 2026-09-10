package com.genai.textrestyler.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genai.textrestyler.data.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSelector(
    selectedStyle: TextStyle,
    onStyleSelected: (TextStyle) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextStyle.entries.forEachIndexed { index, style ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = TextStyle.entries.size
                ),
                onClick = { onStyleSelected(style) },
                selected = style == selectedStyle,
                enabled = enabled
            ) {
                Text(text = style.label)
            }
        }
    }
}
