package com.genai.textrestyler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.genai.textrestyler.ui.mainScreen
import com.genai.textrestyler.ui.theme.GenaiTextRestylerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenaiTextRestylerTheme {
                mainScreen()
            }
        }
    }
}
