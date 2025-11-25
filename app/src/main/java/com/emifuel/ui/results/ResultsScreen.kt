package com.emifuel.ui.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emifuel.model.CalculationResult
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    result: CalculationResult,
    onBack: () -> Unit,
    onSavePdf: () -> Unit
) {
    val decimalFormat = DecimalFormat("#,##0.00")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результати розрахунку") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
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
            // Картка введених даних
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Введені дані",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    HorizontalDivider()

                    DataRow("Технологія спалювання:", result.inputData.combustionTechnology.displayName)
                    DataRow("Технологія десульфуризації:", result.inputData.desulfurizationTechnology.displayName)
                    DataRow("Тип палива:", result.inputData.fuelType.displayName)
                    DataRow(
                        "Витрата палива B:",
                        "${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}"
                    )

                    if (result.inputData.ashContent > 0) {
                        DataRow("Масовий вміст золи Ar:", "${decimalFormat.format(result.inputData.ashContent)} %")
                    }
                    if (result.inputData.lowerHeatingValue > 0) {
                        DataRow("Нижча теплота згоряння Qr:", "${decimalFormat.format(result.inputData.lowerHeatingValue)} МДж/кг")
                    }
                    if (result.inputData.combustiblesInAsh > 0) {
                        DataRow("Вміст горючих Гвин:", "${decimalFormat.format(result.inputData.combustiblesInAsh)} %")
                    }
                    if (result.ashCarryoverValue > 0) {
                        DataRow("Частка леткої золи aвин:", decimalFormat.format(result.ashCarryoverValue))
                    }
                    if (result.dustRemovalEfficiency > 0) {
                        DataRow("Ефективність очищення ηзу:", decimalFormat.format(result.dustRemovalEfficiency))
                    }
                }
            }

            // Картка результатів
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Результати",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    HorizontalDivider()

                    if (result.emissionFactorBeforeClearing > 0) {
                        ResultRow(
                            "Показник емісії (до очищення):",
                            "${decimalFormat.format(result.emissionFactorBeforeClearing)} г/ГДж"
                        )
                    }

                    ResultRow(
                        "Показник емісії kтв (після очищення):",
                        "${decimalFormat.format(result.emissionFactor)} г/ГДж"
                    )
                    ResultRow(
                        "Валовий викид E:",
                        "${decimalFormat.format(result.totalEmission)} т"
                    )
                }
            }

            // Деталі розрахунку формули (2.2)
            if (result.formula22Details.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Деталі розрахунку",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        HorizontalDivider()
                        Text(
                            text = result.formula22Details,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Деталі розрахунку формули (2.1)
            if (result.formula21Details.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = result.formula21Details,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка збереження
            Button(
                onClick = onSavePdf,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти в PDF")
            }
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
