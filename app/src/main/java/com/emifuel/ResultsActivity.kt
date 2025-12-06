package com.emifuel

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.emifuel.model.CalculationResult
import com.emifuel.pdf.PdfGenerator
import com.emifuel.ui.results.ResultsScreen
import com.emifuel.ui.theme.EmiFuelTheme

class ResultsActivity : ComponentActivity() {

    private lateinit var result: CalculationResult
    private val pdfGenerator = PdfGenerator()

    private val createPdfLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            try {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    val success = pdfGenerator.generateReport(result, outputStream)
                    if (success) {
                        Toast.makeText(
                            this,
                            "PDF успішно збережено",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Помилка при збереженні PDF",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Помилка при збереженні PDF: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("CALCULATION_RESULT", CalculationResult::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("CALCULATION_RESULT")
        } ?: run {
            finish()
            return
        }

        setContent {
            EmiFuelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ResultsScreen(
                        result = result,
                        onBack = { finish() },
                        onSavePdf = { savePdf() }
                    )
                }
            }
        }
    }

    private fun savePdf() {
        val fileName = pdfGenerator.generateFileName()
        createPdfLauncher.launch(fileName)
    }
}
