package com.emifuel.calculator

import com.emifuel.model.FuelType
import com.emifuel.model.InputData
import com.emifuel.model.CalculationResult

object EmissionCalculator {

    // Коефіцієнти емісії для різних технологій (г/ГДж)
    private val emissionFactors = mapOf(
        // Вугілля
        "Пилоугольное|Без десульфуризації|COAL" to 1200.0,
        "Пилоугольное|Суха десульфуризація|COAL" to 800.0,
        "Пилоугольное|Мокра десульфуризація|COAL" to 400.0,
        "Циклонне|Без десульфуризації|COAL" to 1500.0,
        "Циклонне|Суха десульфуризація|COAL" to 1000.0,
        "Циклонне|Мокра десульфуризація|COAL" to 500.0,

        // Мазут
        "Пилоугольное|Без десульфуризації|FUEL_OIL" to 600.0,
        "Пилоугольное|Суха десульфуризація|FUEL_OIL" to 400.0,
        "Пилоугольное|Мокра десульфуризація|FUEL_OIL" to 200.0,
        "Циклонне|Без десульфуризації|FUEL_OIL" to 800.0,
        "Циклонне|Суха десульфуризація|FUEL_OIL" to 600.0,
        "Циклонне|Мокра десульфуризація|FUEL_OIL" to 300.0,

        // Газ
        "Пилоугольное|Без десульфуризації|GAS" to 50.0,
        "Пилоугольное|Суха десульфуризація|GAS" to 30.0,
        "Пилоугольное|Мокра десульфуризація|GAS" to 10.0,
        "Циклонне|Без десульфуризації|GAS" to 80.0,
        "Циклонне|Суха десульфуризація|GAS" to 50.0,
        "Циклонне|Мокра десульфуризація|GAS" to 20.0
    )

    // Теплота згоряння палива (ГДж/одиницю)
    private val calorificValues = mapOf(
        FuelType.COAL to 20.0,      // ГДж/тонна
        FuelType.FUEL_OIL to 40.0,  // ГДж/тонна
        FuelType.GAS to 35.0        // ГДж/тис.м³
    )

    fun calculate(inputData: InputData): CalculationResult {
        val key = "${inputData.combustionTechnology}|${inputData.desulfurizationTechnology}|${inputData.fuelType.name}"
        val emissionFactor = emissionFactors[key] ?: 0.0

        val calorificValue = calorificValues[inputData.fuelType] ?: 0.0

        // Валовий викид (т) = Показник емісії (г/ГДж) × Витрата палива × Теплота згоряння / 1_000_000
        val totalEmission = (emissionFactor * inputData.fuelConsumption * calorificValue) / 1_000_000

        return CalculationResult(
            inputData = inputData,
            emissionFactor = emissionFactor,
            totalEmission = totalEmission
        )
    }
}
