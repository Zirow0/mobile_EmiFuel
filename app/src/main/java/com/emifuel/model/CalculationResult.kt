package com.emifuel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalculationResult(
    val inputData: InputData,
    val emissionFactor: Double,  // г/ГДж
    val totalEmission: Double     // тонн
) : Parcelable
