package com.genai.textrestyler.data

enum class TextStyle(
    val label: String,
    val promptModifier: String
) {
    BUSINESS(
        label = "Деловой",
        promptModifier = "деловой (формальный бизнес-стиль, подходящий для деловой переписки)"
    ),
    FORMAL(
        label = "Строгий",
        promptModifier = "строгий (академический, официальный стиль без эмоций)"
    ),
    FRIENDLY(
        label = "Дружеский",
        promptModifier = "дружеский (тёплый, неформальный стиль общения)"
    ),
    HUMOROUS(
        label = "Шуточный",
        promptModifier = "шуточный (юмористический стиль с лёгкой иронией)"
    );
}
