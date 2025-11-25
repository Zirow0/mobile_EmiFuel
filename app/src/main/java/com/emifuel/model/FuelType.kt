package com.emifuel.model

enum class FuelType(val displayName: String, val unit: String) {
    COAL("Донецьке газове вугілля ГР", "тонн"),
    FUEL_OIL("Високосірчистий мазут марки 40", "тонн"),
    GAS("Природний газ (Уренгой-Ужгород)", "тис. м³")
}

// Технології спалювання
enum class CombustionTechnology(val displayName: String) {
    DRY_ASH_REMOVAL("З твердим (сухим) шлаковидаленням"),
    LIQUID_ASH_OPEN("Відкрита топка з рідким шлаковидаленням"),
    LIQUID_ASH_SEMI_OPEN("Напіввідкрита топка з рідким шлаковидаленням"),
    TWO_CHAMBER_VERTICAL("Двокамерна: з вертикальним передтопком"),
    TWO_CHAMBER_HORIZONTAL("Двокамерна: горизонтальна циклонна"),
    CIRCULATING_FLUIDIZED("З циркулюючим киплячим шаром"),
    BUBBLING_FLUIDIZED("З бульбашковим киплячим шаром"),
    FIXED_BED("З нерухомим шаром")
}

// Технології десульфуризації
enum class DesulfurizationTechnology(val displayName: String) {
    NONE("Без десульфуризації"),
    WET_LIMESTONE("Мокре очищення – вапняк/вапно"),
    WET_WELLMAN_LORD("Мокре очищення – процес Веллмана-Лорда"),
    WET_WALTER("Мокре очищення – процес Вальтера"),
    SEMI_DRY_SPRAY("Напівсухе – розпилення суспензії"),
    DRY_INJECTION("Сухе очищення – інжекція сорбенту (DSI)"),
    SEMI_DRY_LIFAC("Напівсухе – процес LIFAC"),
    SEMI_DRY_CFB("Напівсухе – процес Lurgi CFB"),
    DRY_ACTIVATED_CARBON("Сухе – абсорбція активованим вугіллям"),
    CATALYTIC("Каталітичне очищення")
}
