package com.emifuel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputData(
    // Основні параметри
    val combustionTechnology: CombustionTechnology,
    val desulfurizationTechnology: DesulfurizationTechnology,
    val fuelType: FuelType,
    val fuelConsumption: Double, // Витрата палива, т або тис.м³

    // Параметри палива
    val ashContent: Double, // Ar - масовий вміст золи, %
    val lowerHeatingValue: Double, // Qr - нижча теплота згоряння, МДж/кг або МДж/м³
    val combustiblesInAsh: Double, // Гвин - вміст горючих у викидах твердих частинок, %
    val sulfurContent: Double, // Sr - масовий вміст сірки, %

    // Технологічні параметри
    val ashCarryoverFraction: Double, // aвин - частка золи, що виноситься, 0-1
    val dustCollectionEfficiency: Double, // ηзу - ефективність золоуловлювання, 0-1
    val mechanicalIncompleteCombustion: Double // q4 - втрати від механічного недопалу, %
) : Parcelable
