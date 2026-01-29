import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

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

            // Formatiranje rezultata (uklanjanje .0 ako je ceo broj)
            _display.value = formatResult(result)
            firstNumber = null
            currentOperator = null
            isNewNumber = true
        }
    }

    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString().replace(".", ",")
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