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
                text = "Введіть параметри для розрахунку",
                style = MaterialTheme.typography.titleMedium
            )

            // Технологія спалювання
            DropdownField(
                label = "Технологія спалювання",
                options = viewModel.combustionTechnologies,
                selectedOption = uiState.combustionTechnology,
                onOptionSelected = viewModel::onCombustionTechnologyChanged
            )

            // Технологія десульфуризації
            DropdownField(
                label = "Технологія десульфуризації",
                options = viewModel.desulfurizationTechnologies,
                selectedOption = uiState.desulfurizationTechnology,
                onOptionSelected = viewModel::onDesulfurizationTechnologyChanged
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
                    Text("Витрата палива (${uiState.fuelType?.unit ?: "одиниць"})")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

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
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
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
