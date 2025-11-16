package com.emifuel.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emifuel.model.FuelType
import com.emifuel.model.InputData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val combustionTechnology: String = "",
    val desulfurizationTechnology: String = "",
    val fuelType: FuelType? = null,
    val fuelConsumption: String = "",
    val isCalculateEnabled: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val combustionTechnologies = listOf("Пиловугільне", "Циклонне")
    val desulfurizationTechnologies = listOf("Без десульфуризації", "Суха десульфуризація", "Мокра десульфуризація")
    val fuelTypes = FuelType.values().toList()

    fun onCombustionTechnologyChanged(value: String) {
        updateState { copy(combustionTechnology = value) }
    }

    fun onDesulfurizationTechnologyChanged(value: String) {
        updateState { copy(desulfurizationTechnology = value) }
    }

    fun onFuelTypeChanged(value: FuelType) {
        updateState { copy(fuelType = value) }
    }

    fun onFuelConsumptionChanged(value: String) {
        updateState { copy(fuelConsumption = value) }
    }

    fun clearAll() {
        _uiState.value = MainUiState()
    }

    fun getInputData(): InputData? {
        val state = _uiState.value
        return if (state.isCalculateEnabled) {
            InputData(
                combustionTechnology = state.combustionTechnology,
                desulfurizationTechnology = state.desulfurizationTechnology,
                fuelType = state.fuelType!!,
                fuelConsumption = state.fuelConsumption.toDoubleOrNull() ?: 0.0
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
        return state.combustionTechnology.isNotEmpty() &&
                state.desulfurizationTechnology.isNotEmpty() &&
                state.fuelType != null &&
                state.fuelConsumption.toDoubleOrNull()?.let { it > 0 } == true
    }
}
