package com.emifuel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputData(
    val combustionTechnology: String,
    val desulfurizationTechnology: String,
    val fuelType: FuelType,
    val fuelConsumption: Double
) : Parcelable
