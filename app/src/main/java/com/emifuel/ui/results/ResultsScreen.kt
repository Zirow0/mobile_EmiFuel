package com.emifuel.ui.results

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
            StyledCard(title = "Введені дані") {
                DataRow("Технологія спалювання:", result.inputData.combustionTechnology.displayName)
                DataRow("Технологія десульфуризації:", result.inputData.desulfurizationTechnology.displayName)
                DataRow("Тип палива:", result.inputData.fuelType.displayName)
                DataRow(
                    "Витрата палива B:",
                    "${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}"
                )

                // Відображення фільтра (якщо не газ)
                if (result.inputData.fuelType != com.emifuel.model.FuelType.GAS) {
                    DataRow("Тип фільтра:", result.inputData.dustFilterType.displayName)
                }

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

            // Картка результатів
            StyledCard(title = "Результати") {
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

            Spacer(modifier = Modifier.height(8.dp))

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
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun StyledCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            content()
        }
    }
}
