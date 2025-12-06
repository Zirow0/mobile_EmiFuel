package com.emifuel.model

/**
 * Типи фільтрів для очищення димових газів від твердих частинок
 */
enum class DustFilterType(
    val displayName: String,
    val typicalEfficiency: Double,
    val description: String
) {
    NONE(
        displayName = "Немає фільтра",
        typicalEfficiency = 0.0,
        description = "ηзу = 0"
    ),

    ELECTROSTATIC(
        displayName = "Електростатичний фільтр (ЕГА)",
        typicalEfficiency = 0.985,
        description = "ηзу = 0.985"
    ),

    BAG_FILTER(
        displayName = "Рукавний фільтр",
        typicalEfficiency = 0.995,
        description = "ηзу = 0.995"
    ),

    CYCLONE(
        displayName = "Циклон",
        typicalEfficiency = 0.85,
        description = "ηзу = 0.85"
    ),

    MULTICYCLONE(
        displayName = "Мультициклон",
        typicalEfficiency = 0.92,
        description = "ηзу = 0.92"
    ),

    WET_SCRUBBER(
        displayName = "Скрубер (мокрий)",
        typicalEfficiency = 0.96,
        description = "ηзу = 0.96"
    );

    /**
     * Отримання ефективності для типу фільтра
     */
    fun getEfficiency(customValue: Double = 0.0): Double {
        return typicalEfficiency
    }
}
