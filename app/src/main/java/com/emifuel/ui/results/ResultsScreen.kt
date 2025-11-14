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
                title = { Text(" 57C;LB0B8 @>7@0EC=:C") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "0704")
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
            // 0@B:0 22545=8E 40=8E
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
                        text = "2545=V 40=V",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    HorizontalDivider()

                    DataRow(""5E=>;>3VO A?0;N20==O:", result.inputData.combustionTechnology)
                    DataRow(""5E=>;>3VO 45AC;LDC@870FVW:", result.inputData.desulfurizationTechnology)
                    DataRow(""8? ?0;820:", result.inputData.fuelType.displayName)
                    DataRow(
                        "8B@0B0 ?0;820:",
                        "${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}"
                    )
                }
            }

            // 0@B:0 @57C;LB0BV2
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
                        text = " 57C;LB0B8",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    HorizontalDivider()

                    ResultRow(
                        ">:07=8: 5<VAVW:",
                        "${decimalFormat.format(result.emissionFactor)} 3/6"
                    )
                    ResultRow(
                        "0;>289 28:84:",
                        "${decimalFormat.format(result.totalEmission)} B"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // =>?:0 715@565==O
            Button(
                onClick = onSavePdf,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("15@53B8 2 PDF")
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
