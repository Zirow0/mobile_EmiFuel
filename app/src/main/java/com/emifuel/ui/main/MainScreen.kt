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
                title = { Text("EmiFuel - 0;L:C;OB>@ 28:84V2") },
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
                text = "254VBL ?0@0<5B@8 4;O @>7@0EC=:C",
                style = MaterialTheme.typography.titleMedium
            )

            // "5E=>;>3VO A?0;N20==O
            DropdownField(
                label = ""5E=>;>3VO A?0;N20==O",
                options = viewModel.combustionTechnologies,
                selectedOption = uiState.combustionTechnology,
                onOptionSelected = viewModel::onCombustionTechnologyChanged
            )

            // "5E=>;>3VO 45AC;LDC@870FVW
            DropdownField(
                label = ""5E=>;>3VO 45AC;LDC@870FVW",
                options = viewModel.desulfurizationTechnologies,
                selectedOption = uiState.desulfurizationTechnology,
                onOptionSelected = viewModel::onDesulfurizationTechnologyChanged
            )

            // "8? ?0;820
            FuelTypeDropdown(
                fuelTypes = viewModel.fuelTypes,
                selectedFuelType = uiState.fuelType,
                onFuelTypeSelected = viewModel::onFuelTypeChanged
            )

            // 8B@0B0 ?0;820
            OutlinedTextField(
                value = uiState.fuelConsumption,
                onValueChange = viewModel::onFuelConsumptionChanged,
                label = {
                    Text("8B@0B0 ?0;820 (${uiState.fuelType?.unit ?: ">48=8FL"})")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // =>?:8
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::clearAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("G8AB8B8")
                }

                Button(
                    onClick = onNavigateToResults,
                    enabled = uiState.isCalculateEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(" >7@0EC20B8")
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
            label = { Text(""8? ?0;820") },
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
