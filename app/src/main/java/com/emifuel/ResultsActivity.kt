package com.emifuel

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
        val pdfGenerator = PdfGenerator(this)
        val success = pdfGenerator.generatePdf(result)

        if (success) {
            Toast.makeText(this, "PDF CA?VH=> 715@565=> 2 ?0?FV Downloads", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "><8;:0 ?@8 715@565==V PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
