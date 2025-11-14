package com.emifuel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.emifuel.calculator.EmissionCalculator
import com.emifuel.ui.main.MainScreen
import com.emifuel.ui.main.MainViewModel
import com.emifuel.ui.theme.EmiFuelTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmiFuelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        onNavigateToResults = {
                            val inputData = viewModel.getInputData()
                            if (inputData != null) {
                                val result = EmissionCalculator.calculate(inputData)
                                val intent = Intent(this, ResultsActivity::class.java).apply {
                                    putExtra("CALCULATION_RESULT", result)
                                }
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}