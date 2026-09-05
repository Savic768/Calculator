package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.calculator.ui.theme.CalculatorTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            CalculatorTheme {
                IosCalculator()
            }
        }
    }
}

val iOSOrange = Color(0xFFFF9F0A)
val iOSDarkGray = Color(0xFF333333)
val iOSLightGray = Color(0xFFA5A5A5)

@Composable
fun IosCalculator() {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<CalculatorViewModel>()

    val displayText by viewModel.display

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(bottom = 32.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = displayText,
                color = Color.White,
                fontSize = 80.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 20.dp)
            )
        }

        val buttonSpacing = 12.dp

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalButton(text = "AC", color = iOSLightGray, textColor = Color.Black, onClick = { viewModel.onAction("AC") })
            CalButton(text = "+/-", color = iOSLightGray, textColor = Color.Black, onClick = { viewModel.onAction("+/-") })
            CalButton(text = "%", color = iOSLightGray, textColor = Color.Black, onClick = { viewModel.onAction("%") })
            CalButton(text = "÷", color = iOSOrange, onClick = { viewModel.onAction("÷") })
        }
        Spacer(modifier = Modifier.height(buttonSpacing))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalButton(text = "7", color = iOSDarkGray, onClick = { viewModel.onAction("7") })
            CalButton(text = "8", color = iOSDarkGray, onClick = { viewModel.onAction("8") })
            CalButton(text = "9", color = iOSDarkGray, onClick = { viewModel.onAction("9") })
            CalButton(text = "×", color = iOSOrange, onClick = { viewModel.onAction("×") })
        }
        Spacer(modifier = Modifier.height(buttonSpacing))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalButton(text = "4", color = iOSDarkGray, onClick = { viewModel.onAction("4") })
            CalButton(text = "5", color = iOSDarkGray, onClick = { viewModel.onAction("5") })
            CalButton(text = "6", color = iOSDarkGray, onClick = { viewModel.onAction("6") })
            CalButton(text = "-", color = iOSOrange, onClick = { viewModel.onAction("-") })
        }
        Spacer(modifier = Modifier.height(buttonSpacing))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalButton(text = "1", color = iOSDarkGray, onClick = { viewModel.onAction("1") })
            CalButton(text = "2", color = iOSDarkGray, onClick = { viewModel.onAction("2") })
            CalButton(text = "3", color = iOSDarkGray, onClick = { viewModel.onAction("3") })
            CalButton(text = "+", color = iOSOrange, onClick = { viewModel.onAction("+") })
        }
        Spacer(modifier = Modifier.height(buttonSpacing))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CalButton(text = "0", color = iOSDarkGray, weight = 2f, aspectRatio = 2.05f, alignment = Alignment.CenterStart, onClick = { viewModel.onAction("0") })
            CalButton(text = ",", color = iOSDarkGray, onClick = { viewModel.onAction(",") })
            CalButton(text = "=", color = iOSOrange, onClick = { viewModel.onAction("=") })
        }
    }
}

@Composable
fun RowScope.CalButton(
    text: String,
    color: Color,
    textColor: Color = Color.White,
    weight: Float = 1f,
    aspectRatio: Float = 1f,
    alignment: Alignment = Alignment.Center,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .weight(weight)
            .aspectRatio(aspectRatio)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 36.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = if (weight > 1f) 30.dp else 0.dp)
        )
    }
}

class CalculatorViewModel : ViewModel() {
    private val _display = mutableStateOf("0")
    val display: State<String> = _display

    private var firstNumber: Double? = null
    private var currentOperator: String? = null
    private var isNewNumber = true

    fun onAction(action: String) {
        when {
            action.all { it.isDigit() } -> enterNumber(action)
            action == "AC" -> clear()
            action == "+/-" -> toggleSign()
            action == "%" -> percentage()
            action == "," -> enterDecimal()
            action == "=" -> calculate()
            else -> enterOperator(action)
        }
    }

    private fun enterNumber(number: String) {
        if (_display.value == "0" || isNewNumber) {
            _display.value = number
        } else {
            _display.value += number
        }
        isNewNumber = false
    }

    private fun enterOperator(operator: String) {
        firstNumber = _display.value.replace(",", ".").toDoubleOrNull()
        currentOperator = operator
        isNewNumber = true
    }

    private fun calculate() {
        val secondNumber = _display.value.replace(",", ".").toDoubleOrNull()
        if (firstNumber != null && secondNumber != null && currentOperator != null) {
            val result = when (currentOperator) {
                "+" -> firstNumber!! + secondNumber
                "-" -> firstNumber!! - secondNumber
                "×" -> firstNumber!! * secondNumber
                "÷" -> if (secondNumber != 0.0) firstNumber!! / secondNumber else Double.NaN
                else -> return
            }

            _display.value = formatResult(result)
            firstNumber = null
            currentOperator = null
            isNewNumber = true
        }
    }

    private fun formatResult(result: Double): String {
        if (result % 1 == 0.0) {
            return result.toLong().toString()
        }
        val formatted = String.format("%.10f", result)
        var cleaned = formatted.replace(" ", "")

        if (cleaned.contains(".")) {
            val parts = cleaned.split(".")
            var integerPart = parts[0]
            var decimalPart = parts[1]

            if (decimalPart.length > 2) {
                decimalPart = decimalPart.substring(0, 2)
            }
            return "${integerPart}.${decimalPart}"
        } else {
            return cleaned
        }
    }

    private fun clear() {
        _display.value = "0"
        firstNumber = null
        currentOperator = null
        isNewNumber = true
    }

    private fun toggleSign() {
        if (_display.value != "0") {
            if (_display.value.startsWith("-")) {
                _display.value = _display.value.drop(1)
            } else {
                _display.value = "-" + _display.value
            }
        }
    }

    private fun percentage() {
        val current = _display.value.replace(",", ".").toDoubleOrNull() ?: return
        _display.value = formatResult(current / 100)
    }

    private fun enterDecimal() {
        if (!_display.value.contains(",")) {
            _display.value += ","
            isNewNumber = false
        }
    }
}