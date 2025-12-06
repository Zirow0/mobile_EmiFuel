package com.emifuel.calculator

import com.emifuel.model.*
import kotlin.math.round

object EmissionCalculator {

    // Таблиця 2.1: Частка леткої золи aвин при різних технологіях спалювання
    private val ashCarryoverFractions = mapOf(
        // Вугілля
        CombustionTechnology.DRY_ASH_REMOVAL to mapOf(FuelType.COAL to 0.95, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.LIQUID_ASH_OPEN to mapOf(FuelType.COAL to 0.80, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.LIQUID_ASH_SEMI_OPEN to mapOf(FuelType.COAL to 0.70, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.TWO_CHAMBER_VERTICAL to mapOf(FuelType.COAL to 0.55, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.TWO_CHAMBER_HORIZONTAL to mapOf(FuelType.COAL to 0.30, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.CIRCULATING_FLUIDIZED to mapOf(FuelType.COAL to 0.50, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.BUBBLING_FLUIDIZED to mapOf(FuelType.COAL to 0.20, FuelType.FUEL_OIL to 1.00),
        CombustionTechnology.FIXED_BED to mapOf(FuelType.COAL to 0.15, FuelType.FUEL_OIL to 1.00)
    )

    // Константа: теплота згоряння вуглецю до CO2
    private const val QC = 32.68 // МДж/кг

    /**
     * Розрахунок валового викиду твердих частинок за формулами (2.1) і (2.2)
     */
    fun calculate(inputData: InputData): CalculationResult {
        // Отримуємо частку леткої золи з таблиці 2.1
        val ashCarryoverFromTable = getAshCarryoverFraction(
            inputData.combustionTechnology,
            inputData.fuelType
        )

        // Використовуємо значення з таблиці або введене користувачем
        val ashCarryover = if (inputData.ashCarryoverFraction > 0) {
            inputData.ashCarryoverFraction
        } else {
            ashCarryoverFromTable
        }

        // Для природного газу твердих частинок немає
        if (inputData.fuelType == FuelType.GAS) {
            return CalculationResult(
                inputData = inputData,
                ashCarryoverValue = 0.0,
                dustRemovalEfficiency = 0.0,
                emissionFactorBeforeClearing = 0.0,
                emissionFactor = 0.0,
                totalEmission = 0.0,
                formula22Details = "Для природного газу: kтв = 0 г/ГДж (тверді частинки відсутні)",
                formula21Details = "Для природного газу: Eтв = 0 т (тверді частинки відсутні)"
            )
        }

        // Формула (2.2): Розрахунок показника емісії твердих частинок
        val emissionFactorBeforeClearing = calculateEmissionFactor(
            Qr = inputData.lowerHeatingValue,
            Ar = inputData.ashContent,
            avin = ashCarryover,
            Gvin = inputData.combustiblesInAsh,
            q4 = inputData.mechanicalIncompleteCombustion
        )

        // Отримуємо ефективність золоуловлювання з урахуванням типу фільтра
        val actualDustEfficiency = inputData.calculateDustCollectionEfficiency()

        // Показник емісії після очищення
        val emissionFactor = emissionFactorBeforeClearing * (1 - actualDustEfficiency)

        // Формула (2.1): Розрахунок валового викиду
        val totalEmission = calculateTotalEmission(
            k = emissionFactor,
            B = inputData.fuelConsumption,
            Qr = inputData.lowerHeatingValue
        )

        // Деталі розрахунків для відображення
        val formula22Details = buildFormula22Details(
            Qr = inputData.lowerHeatingValue,
            Ar = inputData.ashContent,
            avin = ashCarryover,
            Gvin = inputData.combustiblesInAsh,
            q4 = inputData.mechanicalIncompleteCombustion,
            etazu = actualDustEfficiency,
            filterType = inputData.dustFilterType,
            ktvBefore = emissionFactorBeforeClearing,
            ktvAfter = emissionFactor
        )

        val formula21Details = buildFormula21Details(
            k = emissionFactor,
            B = inputData.fuelConsumption,
            Qr = inputData.lowerHeatingValue,
            E = totalEmission,
            fuelType = inputData.fuelType
        )

        return CalculationResult(
            inputData = inputData,
            ashCarryoverValue = ashCarryover,
            dustRemovalEfficiency = actualDustEfficiency,
            emissionFactorBeforeClearing = round(emissionFactorBeforeClearing * 100) / 100,
            emissionFactor = round(emissionFactor * 100) / 100,
            totalEmission = round(totalEmission * 100) / 100,
            formula22Details = formula22Details,
            formula21Details = formula21Details
        )
    }

    /**
     * Формула (2.3): kтв(до) = (10⁶ / Qr) × aвин × (Ar / (100 - Гвин)) × (1 - q4/QC)
     * де QC = 32.68 МДж/кг - теплота згоряння вуглецю до CO2
     */
    private fun calculateEmissionFactor(
        Qr: Double,   // Нижча теплота згоряння, МДж/кг
        Ar: Double,   // Масовий вміст золи, %
        avin: Double, // Частка леткої золи
        Gvin: Double, // Вміст горючих у викидах, %
        q4: Double    // Втрати від механічного недопалу, %
    ): Double {
        // Базовий показник емісії без врахування q4
        val factorWithoutQ4 = (1_000_000.0 / Qr) * avin * (Ar / (100.0 - Gvin))

        // Враховуємо втрати від механічного недопалу (формула 2.3)
        val ktvBefore = factorWithoutQ4 * (1 - q4 / QC)

        return ktvBefore
    }

    /**
     * Формула (2.1): E = 10⁻⁶ × k × B × Qr
     */
    private fun calculateTotalEmission(
        k: Double,  // Показник емісії, г/ГДж
        B: Double,  // Витрата палива, т або тис.м³
        Qr: Double  // Нижча теплота згоряння, МДж/кг або МДж/м³
    ): Double {
        return 1e-6 * k * B * Qr
    }

    /**
     * Отримання частки леткої золи з таблиці 2.1
     */
    private fun getAshCarryoverFraction(
        combustionTech: CombustionTechnology,
        fuelType: FuelType
    ): Double {
        return ashCarryoverFractions[combustionTech]?.get(fuelType) ?: 1.0
    }

    /**
     * Деталі розрахунку формул (2.3) та (2.2)
     */
    private fun buildFormula22Details(
        Qr: Double,
        Ar: Double,
        avin: Double,
        Gvin: Double,
        q4: Double,
        etazu: Double,
        filterType: DustFilterType,
        ktvBefore: Double,
        ktvAfter: Double
    ): String {
        return buildString {
            appendLine("Формула (2.3): Показник емісії до очищення (з урахуванням q4)")
            appendLine()
            appendLine("kтв(до) = (10⁶ / Qr) × aвин × (Ar / (100 - Гвин)) × (1 - q4/QC)")
            appendLine("де QC = 32.68 МДж/кг")
            appendLine()
            appendLine("Вхідні дані:")
            appendLine("  Qr = ${String.format("%.2f", Qr)} МДж/кг")
            appendLine("  Ar = ${String.format("%.2f", Ar)} %")
            appendLine("  aвин = ${String.format("%.2f", avin)} (частка леткої золи)")
            appendLine("  Гвин = ${String.format("%.2f", Gvin)} %")
            appendLine("  q4 = ${String.format("%.2f", q4)} % (втрати від недопалу)")
            appendLine("  Тип фільтра: ${filterType.displayName}")
            appendLine("  ηзу = ${String.format("%.3f", etazu)} (ефективність очищення)")
            appendLine()
            appendLine("Розрахунок:")
            appendLine("  kтв(до) = (10⁶ / ${String.format("%.2f", Qr)}) × ${String.format("%.2f", avin)} × (${String.format("%.2f", Ar)} / (100 - ${String.format("%.2f", Gvin)})) × (1 - ${String.format("%.2f", q4)}/32.68)")
            appendLine("  kтв(до очищення) = ${String.format("%.2f", ktvBefore)} г/ГДж")
            appendLine()
            appendLine("Формула (2.2): Показник емісії після очищення")
            appendLine("  kтв = kтв(до) × (1 - ηзу)")
            appendLine("  kтв = ${String.format("%.2f", ktvBefore)} × (1 - ${String.format("%.3f", etazu)})")
            appendLine("  kтв(після очищення) = ${String.format("%.2f", ktvAfter)} г/ГДж")
        }
    }

    /**
     * Деталі розрахунку формули (2.1)
     */
    private fun buildFormula21Details(
        k: Double,
        B: Double,
        Qr: Double,
        E: Double,
        fuelType: FuelType
    ): String {
        return buildString {
            appendLine("Формула (2.1): Валовий викид")
            appendLine()
            appendLine("E = 10⁻⁶ × k × B × Qr")
            appendLine()
            appendLine("Вхідні дані:")
            appendLine("  k = ${String.format("%.2f", k)} г/ГДж")
            appendLine("  B = ${String.format("%.2f", B)} ${fuelType.unit}")
            appendLine("  Qr = ${String.format("%.2f", Qr)} МДж/${if (fuelType == FuelType.GAS) "м³" else "кг"}")
            appendLine()
            appendLine("Розрахунок:")
            appendLine("  E = 10⁻⁶ × ${String.format("%.2f", k)} × ${String.format("%.2f", B)} × ${String.format("%.2f", Qr)}")
            appendLine("  E = ${String.format("%.2f", E)} т")
        }
    }
}
