package com.emifuel.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emifuel.model.CombustionTechnology
import com.emifuel.model.DesulfurizationTechnology
import com.emifuel.model.DustFilterType
import com.emifuel.model.FuelData
import com.emifuel.model.FuelType
import com.emifuel.model.InputData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val combustionTechnology: CombustionTechnology? = null,
    val desulfurizationTechnology: DesulfurizationTechnology? = null,
    val fuelType: FuelType? = null,
    val fuelConsumption: String = "",

    // Нові поля з завдання
    val ashContent: String = "", // Ar - масовий вміст золи, %
    val lowerHeatingValue: String = "", // Qr - нижча теплота згоряння, МДж/кг
    val combustiblesInAsh: String = "", // Гвин - вміст горючих у викидах, %
    val sulfurContent: String = "", // Sr - масовий вміст сірки, %
    val ashCarryoverFraction: String = "", // aвин - частка золи, що виноситься, 0-1
    val dustFilterType: DustFilterType = DustFilterType.ELECTROSTATIC, // Тип фільтра
    val dustCollectionEfficiency: String = "", // ηзу - ефективність золоуловлювання, 0-1
    val mechanicalIncompleteCombustion: String = "", // q4 - втрати від недопалу, %

    val isCalculateEnabled: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val combustionTechnologies = CombustionTechnology.values().toList()
    val desulfurizationTechnologies = DesulfurizationTechnology.values().toList()
    val fuelTypes = FuelType.values().toList()
    val dustFilterTypes = DustFilterType.values().toList()

    fun onCombustionTechnologyChanged(value: CombustionTechnology) {
        updateState { copy(combustionTechnology = value) }
    }

    fun onDesulfurizationTechnologyChanged(value: DesulfurizationTechnology) {
        updateState { copy(desulfurizationTechnology = value) }
    }

    fun onFuelTypeChanged(value: FuelType) {
        val state = _uiState.value
        // Автоматично підставляємо значення з таблиць А.1-А.4
        val characteristics = FuelData.getCharacteristics(value)

        // Для газу встановлюємо фільтр "Немає" та ηзу = 0
        val filterType = if (value == FuelType.GAS) DustFilterType.NONE else state.dustFilterType
        val efficiency = if (value == FuelType.GAS) "0" else filterType.typicalEfficiency.toString()

        updateState {
            copy(
                fuelType = value,
                ashContent = if (value == FuelType.GAS) "" else characteristics.ashContent.toString(),
                lowerHeatingValue = characteristics.lowerHeatingValue.toString(),
                combustiblesInAsh = if (value == FuelType.GAS) "" else characteristics.combustiblesInAsh.toString(),
                sulfurContent = if (value == FuelType.GAS) "" else characteristics.sulfurContent.toString(),
                dustFilterType = filterType,
                dustCollectionEfficiency = efficiency
            )
        }
    }

    fun onDustFilterTypeChanged(value: DustFilterType) {
        // Автоматично оновлюємо ηзу на типове значення для фільтра
        val efficiency = value.typicalEfficiency.toString()

        updateState {
            copy(
                dustFilterType = value,
                dustCollectionEfficiency = efficiency
            )
        }
    }

    fun onFuelConsumptionChanged(value: String) {
        updateState { copy(fuelConsumption = value) }
    }

    fun onAshContentChanged(value: String) {
        updateState { copy(ashContent = value) }
    }

    fun onLowerHeatingValueChanged(value: String) {
        updateState { copy(lowerHeatingValue = value) }
    }

    fun onCombustiblesInAshChanged(value: String) {
        updateState { copy(combustiblesInAsh = value) }
    }

    fun onSulfurContentChanged(value: String) {
        updateState { copy(sulfurContent = value) }
    }

    fun onAshCarryoverFractionChanged(value: String) {
        updateState { copy(ashCarryoverFraction = value) }
    }

    fun onDustCollectionEfficiencyChanged(value: String) {
        updateState { copy(dustCollectionEfficiency = value) }
    }

    fun onMechanicalIncompleteCombustionChanged(value: String) {
        updateState { copy(mechanicalIncompleteCombustion = value) }
    }

    fun clearAll() {
        _uiState.value = MainUiState()
    }

    fun getInputData(): InputData? {
        val state = _uiState.value
        return if (state.isCalculateEnabled) {
            InputData(
                combustionTechnology = state.combustionTechnology!!,
                desulfurizationTechnology = state.desulfurizationTechnology!!,
                fuelType = state.fuelType!!,
                fuelConsumption = state.fuelConsumption.toDoubleOrNull() ?: 0.0,
                ashContent = state.ashContent.toDoubleOrNull() ?: 0.0,
                lowerHeatingValue = state.lowerHeatingValue.toDoubleOrNull() ?: 0.0,
                combustiblesInAsh = state.combustiblesInAsh.toDoubleOrNull() ?: 0.0,
                sulfurContent = state.sulfurContent.toDoubleOrNull() ?: 0.0,
                ashCarryoverFraction = state.ashCarryoverFraction.toDoubleOrNull() ?: 0.0,
                dustFilterType = state.dustFilterType,
                dustCollectionEfficiency = state.dustCollectionEfficiency.toDoubleOrNull() ?: 0.0,
                mechanicalIncompleteCombustion = state.mechanicalIncompleteCombustion.toDoubleOrNull() ?: 0.0
            )
        } else null
    }

    private fun updateState(update: MainUiState.() -> MainUiState) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val newState = currentState.update()
                newState.copy(isCalculateEnabled = validateInput(newState))
            }
        }
    }

    private fun validateInput(state: MainUiState): Boolean {
        // Базові поля
        val basicValid = state.combustionTechnology != null &&
                state.desulfurizationTechnology != null &&
                state.fuelType != null &&
                state.fuelConsumption.toDoubleOrNull()?.let { it > 0 } == true

        // Для природного газу не потрібні параметри золи
        if (state.fuelType == FuelType.GAS) {
            return basicValid
        }

        // Для вугілля та мазуту потрібні додаткові параметри
        return basicValid &&
                state.ashContent.toDoubleOrNull()?.let { it >= 0 } == true &&
                state.lowerHeatingValue.toDoubleOrNull()?.let { it > 0 } == true &&
                state.combustiblesInAsh.toDoubleOrNull()?.let { it >= 0 } == true &&
                state.dustCollectionEfficiency.toDoubleOrNull()?.let { it >= 0 && it <= 1 } == true
    }
}
