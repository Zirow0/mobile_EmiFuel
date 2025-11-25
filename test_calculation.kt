import com.emifuel.calculator.EmissionCalculator
import com.emifuel.model.*

/**
 * Тестовий розрахунок для перевірки на контрольному прикладі з завдання
 *
 * Очікувані результати для вугілля:
 * - Показник емісії kтв = 150 г/ГДж
 * - Валовий викид E = 3366 т
 */

fun main() {
    // Дані з контрольного прикладу (приблизні, на основі типових значень для Донецького вугілля)
    val inputData = InputData(
        combustionTechnology = CombustionTechnology.DRY_ASH_REMOVAL, // aвин = 0.95
        desulfurizationTechnology = DesulfurizationTechnology.NONE,
        fuelType = FuelType.COAL,
        fuelConsumption = 150000.0, // 150 тис. тонн
        ashContent = 25.0,  // Ar = 25% (типове для Донецького вугілля)
        lowerHeatingValue = 24.0, // Qr = 24 МДж/кг (типове)
        combustiblesInAsh = 5.0, // Гвин = 5%
        sulfurContent = 2.5, // Sr = 2.5%
        ashCarryoverFraction = 0.0, // Використовуємо табличне значення
        dustCollectionEfficiency = 0.0, // Без очищення для контрольного прикладу
        mechanicalIncompleteCombustion = 0.0 // q4 = 0%
    )

    val result = EmissionCalculator.calculate(inputData)

    println("=".repeat(60))
    println("КОНТРОЛЬНИЙ ПРИКЛАД - ПЕРЕВІРКА РОЗРАХУНКІВ")
    println("=".repeat(60))
    println()
    println("Вхідні дані:")
    println("  Технологія спалювання: ${inputData.combustionTechnology.displayName}")
    println("  Тип палива: ${inputData.fuelType.displayName}")
    println("  Витрата палива B: ${inputData.fuelConsumption} тонн")
    println("  Масовий вміст золи Ar: ${inputData.ashContent} %")
    println("  Нижча теплота згоряння Qr: ${inputData.lowerHeatingValue} МДж/кг")
    println("  Вміст горючих Гвин: ${inputData.combustiblesInAsh} %")
    println("  Частка леткої золи aвин: ${result.ashCarryoverValue}")
    println("  Ефективність очищення ηзу: ${result.dustRemovalEfficiency}")
    println()
    println("-".repeat(60))
    println()
    println(result.formula22Details)
    println()
    println("-".repeat(60))
    println()
    println(result.formula21Details)
    println()
    println("=".repeat(60))
    println("ПОРІВНЯННЯ З ОЧІКУВАНИМИ ЗНАЧЕННЯМИ:")
    println("=".repeat(60))
    println()

    val expectedEmissionFactor = 150.0
    val expectedTotalEmission = 3366.0

    val emissionFactorDiff = kotlin.math.abs(result.emissionFactor - expectedEmissionFactor)
    val totalEmissionDiff = kotlin.math.abs(result.totalEmission - expectedTotalEmission)

    println("Показник емісії kтв:")
    println("  Розраховано: ${String.format("%.2f", result.emissionFactor)} г/ГДж")
    println("  Очікується:  ${String.format("%.2f", expectedEmissionFactor)} г/ГДж")
    println("  Різниця:     ${String.format("%.2f", emissionFactorDiff)} г/ГДж")
    println()
    println("Валовий викид E:")
    println("  Розраховано: ${String.format("%.2f", result.totalEmission)} т")
    println("  Очікується:  ${String.format("%.2f", expectedTotalEmission)} т")
    println("  Різниця:     ${String.format("%.2f", totalEmissionDiff)} т")
    println()

    // Перевірка з допуском ±10%
    val emissionFactorMatch = (emissionFactorDiff / expectedEmissionFactor) < 0.1
    val totalEmissionMatch = (totalEmissionDiff / expectedTotalEmission) < 0.1

    if (emissionFactorMatch && totalEmissionMatch) {
        println("✓ ТЕСТ ПРОЙДЕНО: Результати відповідають очікуваним значенням")
    } else {
        println("✗ УВАГА: Результати відрізняються від очікуваних")
        if (!emissionFactorMatch) {
            println("  - Показник емісії виходить за межі допуску ±10%")
        }
        if (!totalEmissionMatch) {
            println("  - Валовий викид виходить за межі допуску ±10%")
        }
    }
    println()
    println("=".repeat(60))
}
