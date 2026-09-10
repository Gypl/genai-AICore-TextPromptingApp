# ML Kit GenAI
-keep class com.google.mlkit.genai.** { *; }
-dontwarn com.google.mlkit.genai.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Keep application classes
-keep class com.genai.textrestyler.data.** { *; }
-keep class com.genai.textrestyler.ai.** { *; }
