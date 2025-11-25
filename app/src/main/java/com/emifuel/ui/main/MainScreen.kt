package com.emifuel.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emifuel.model.CombustionTechnology
import com.emifuel.model.DesulfurizationTechnology
import com.emifuel.model.FuelType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToResults: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EmiFuel - Калькулятор викидів") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Основні параметри",
                style = MaterialTheme.typography.titleMedium
            )

            // Технологія спалювання
            CombustionTechnologyDropdown(
                technologies = viewModel.combustionTechnologies,
                selectedTechnology = uiState.combustionTechnology,
                onTechnologySelected = viewModel::onCombustionTechnologyChanged
            )

            // Технологія десульфуризації
            DesulfurizationTechnologyDropdown(
                technologies = viewModel.desulfurizationTechnologies,
                selectedTechnology = uiState.desulfurizationTechnology,
                onTechnologySelected = viewModel::onDesulfurizationTechnologyChanged
            )

            // Тип палива
            FuelTypeDropdown(
                fuelTypes = viewModel.fuelTypes,
                selectedFuelType = uiState.fuelType,
                onFuelTypeSelected = viewModel::onFuelTypeChanged
            )

            // Витрата палива
            OutlinedTextField(
                value = uiState.fuelConsumption,
                onValueChange = viewModel::onFuelConsumptionChanged,
                label = {
                    Text("Витрата палива B (${uiState.fuelType?.unit ?: "одиниць"})")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Ефективність золоуловлювання (тільки для вугілля та мазуту)
            if (uiState.fuelType != null && uiState.fuelType != FuelType.GAS) {
                OutlinedTextField(
                    value = uiState.dustCollectionEfficiency,
                    onValueChange = viewModel::onDustCollectionEfficiencyChanged,
                    label = { Text("Ефективність золоуловлювання ηзу (0-1)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("Наприклад: 0.985 для ЕГА") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::clearAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Очистити")
                }

                Button(
                    onClick = onNavigateToResults,
                    enabled = uiState.isCalculateEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Розрахувати")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombustionTechnologyDropdown(
    technologies: List<CombustionTechnology>,
    selectedTechnology: CombustionTechnology?,
    onTechnologySelected: (CombustionTechnology) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedTechnology?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Технологія спалювання") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            technologies.forEach { tech ->
                DropdownMenuItem(
                    text = { Text(tech.displayName) },
                    onClick = {
                        onTechnologySelected(tech)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesulfurizationTechnologyDropdown(
    technologies: List<DesulfurizationTechnology>,
    selectedTechnology: DesulfurizationTechnology?,
    onTechnologySelected: (DesulfurizationTechnology) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedTechnology?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Технологія десульфуризації") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            technologies.forEach { tech ->
                DropdownMenuItem(
                    text = { Text(tech.displayName) },
                    onClick = {
                        onTechnologySelected(tech)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeDropdown(
    fuelTypes: List<FuelType>,
    selectedFuelType: FuelType?,
    onFuelTypeSelected: (FuelType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedFuelType?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Тип палива") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fuelTypes.forEach { fuelType ->
                DropdownMenuItem(
                    text = { Text(fuelType.displayName) },
                    onClick = {
                        onFuelTypeSelected(fuelType)
                        expanded = false
                    }
                )
            }
        }
    }
}
