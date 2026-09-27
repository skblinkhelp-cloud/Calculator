package com.sk.calculator_aisupported

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sk.calculator_aisupported.ui.CalculatorInterface
import com.sk.calculator_aisupported.ui.CalculatorViewModel
import com.sk.calculator_aisupported.ui.theme.CalculatorAISupportedTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            CalculatorAISupportedTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) Color.Black else Color(0xFFF2F2F7)
                ) {
                    CalculatorInterface(viewModel = viewModel)
                }
            }
        }
    }
}