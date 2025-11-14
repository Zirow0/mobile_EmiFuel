package com.emifuel.model

enum class FuelType(val displayName: String, val unit: String) {
    COAL("Вугілля", "тонн"),
    FUEL_OIL("Мазут", "тонн"),
    GAS("Газ", "тис. м³")
}
