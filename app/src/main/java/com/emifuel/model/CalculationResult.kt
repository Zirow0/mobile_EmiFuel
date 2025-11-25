package com.emifuel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalculationResult(
    val inputData: InputData,

    // Проміжні розрахунки
    val ashCarryoverValue: Double, // aвин для конкретної технології
    val dustRemovalEfficiency: Double, // ηзу

    // Результати формули (2.2)
    val emissionFactorBeforeClearing: Double, // kтв до очищення, г/ГДж
    val emissionFactor: Double,  // kтв після очищення, г/ГДж

    // Результат формули (2.1)
    val totalEmission: Double,    // Валовий викид, тонн

    // Для відображення деталей
    val formula22Details: String, // Деталі розрахунку формули 2.2
    val formula21Details: String  // Деталі розрахунку формули 2.1
) : Parcelable
