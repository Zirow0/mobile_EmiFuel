package com.emifuel.model

/**
 * Характеристики палива з таблиць А.1, А.3, А.4 додатку А
 */
data class FuelCharacteristics(
    val ashContent: Double,           // Ar - масовий вміст золи, %
    val lowerHeatingValue: Double,    // Qr - нижча теплота згоряння, МДж/кг або МДж/м³
    val sulfurContent: Double,        // Sr - масовий вміст сірки, %
    val combustiblesInAsh: Double     // Гвин - вміст горючих у викидах, %
)

/**
 * Дані з таблиці А.1 (вугілля) та А.3 (мазут), А.4 (газ)
 */
object FuelData {

    // Таблиця А.1: Донецьке газове вугілля ГР (горюча маса)
    // C=81.0%, H=5.4%, S=4.4%, O=7.7%, N=1.5%, V=40.0%, Q=31.98 МДж/кг
    // Приблизні значення для робочої маси з урахуванням золи та вологи
    val DONETSK_COAL = FuelCharacteristics(
        ashContent = 25.20,          // Ar з умови завдання
        lowerHeatingValue = 20.47,   // Qr з умови завдання, МДж/кг
        sulfurContent = 2.85,        // Sr з умови завдання
        combustiblesInAsh = 1.5      // Гвин з умови завдання
    )

    // Таблиця А.3: Високосірчистий мазут марки 40
    // Середні значення: S=2.50%, C=85.50%, H=11.20%, (O+N)=0.80%
    // Q горючої = 40.40 МДж/кг
    // Ar (суха маса) = 0.15%, Wr = 2.00%
    val FUEL_OIL_40 = FuelCharacteristics(
        ashContent = 0.15,           // Ar (зольність сухої маси)
        lowerHeatingValue = 39.48,   // Qr робоча = Q_daf * (100-W-A)/100, МДж/кг
        sulfurContent = 2.50,        // Sr
        combustiblesInAsh = 0.0      // Для мазуту Гвин = 0%
    )

    // Таблиця А.4: Природний газ Уренгой-Ужгород
    // CH4=98.90%, C2H6=0.12%, C3H8=0.011%, C4H10=0.01%
    // CO2=0.06%, N2=0.90%, H2S=0.00%
    // Q = 33.08 МДж/м³, ρ = 0.723 кг/м³
    val NATURAL_GAS = FuelCharacteristics(
        ashContent = 0.0,            // Для газу золи немає
        lowerHeatingValue = 33.08,   // Qr, МДж/м³
        sulfurContent = 0.0,         // Для природного газу Уренгой-Ужгород Sr ≈ 0
        combustiblesInAsh = 0.0      // Для газу твердих частинок немає
    )

    /**
     * Отримання характеристик палива за типом
     */
    fun getCharacteristics(fuelType: FuelType): FuelCharacteristics {
        return when (fuelType) {
            FuelType.COAL -> DONETSK_COAL
            FuelType.FUEL_OIL -> FUEL_OIL_40
            FuelType.GAS -> NATURAL_GAS
        }
    }
}
