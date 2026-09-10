package com.genai.textrestyler.ai

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Результат проверки совместимости устройства с AICore.
 */
sealed class DeviceCheckResult {
    /** Устройство полностью совместимо. */
    data object Compatible : DeviceCheckResult()

    /** Устройство не совместимо — содержит подробную информацию о причине. */
    data class Incompatible(val info: DeviceInfo) : DeviceCheckResult()
}

/**
 * Информация об устройстве и причина несовместимости.
 */
data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val device: String,
    val androidVersion: String,
    val sdkVersion: Int,
    val isAiCoreInstalled: Boolean,
    val failureReasons: List<String>
)

@Singleton
class DeviceChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val AICORE_PACKAGE = "com.google.android.aicore"
        private const val MIN_SDK_VERSION = 31 // Android 12 (AICore минимально требует Android 12+)
    }

    /**
     * Проверяет совместимость устройства:
     * 1. Версия Android (SDK >= 31)
     * 2. Наличие установленного пакета AICore
     */
    fun check(): DeviceCheckResult {
        val manufacturer = Build.MANUFACTURER ?: "Неизвестный"
        val model = Build.MODEL ?: "Неизвестная модель"
        val device = Build.DEVICE ?: "Неизвестное устройство"
        val androidVersion = Build.VERSION.RELEASE ?: "Неизвестная"
        val sdkVersion = Build.VERSION.SDK_INT
        val aiCoreInstalled = isAiCoreInstalled()

        val reasons = mutableListOf<String>()

        if (sdkVersion < MIN_SDK_VERSION) {
            reasons.add(
                "Версия Android ($androidVersion, API $sdkVersion) ниже минимально необходимой " +
                "(Android 12, API $MIN_SDK_VERSION). Для работы AI-функций обновите ОС."
            )
        }

        if (!aiCoreInstalled) {
            reasons.add(
                "Системный сервис AICore (${AICORE_PACKAGE}) не найден на устройстве. " +
                "AICore необходим для запуска on-device AI моделей (Gemini Nano). " +
                "Убедитесь, что ваше устройство поддерживает AICore и Google Play Services обновлены."
            )
        }

        return if (reasons.isEmpty()) {
            DeviceCheckResult.Compatible
        } else {
            DeviceCheckResult.Incompatible(
                DeviceInfo(
                    manufacturer = manufacturer,
                    model = model,
                    device = device,
                    androidVersion = androidVersion,
                    sdkVersion = sdkVersion,
                    isAiCoreInstalled = aiCoreInstalled,
                    failureReasons = reasons
                )
            )
        }
    }

    private fun isAiCoreInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(AICORE_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
