package com.example.calculator_1.core.base.controller


import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.calculator_1.core.data.Operator
import com.example.calculator_1.databinding.ActivityMainBinding
import java.lang.Exception


open class ClickController(private val screen: ActivityMainBinding) {

    val LARGE_TEXT_SIZE = 72F
    val MEDIUM_TEXT_SIZE = 48F
    val SMALL_TEXT_SIZE = 36F


    fun setOnclickListeners() {

        setListenerOnInputField()

        setClickOnBtnEqListener()
        setClickOnBtnBracketsListener()
        setClickOnBtnClearListener()
        setClickOnClearPrevBtnListener()
        setClickOnNumbersListener()
        setClickOnOperatorListener()
        setClickOnSwitchSignListener()
    }

    private fun setListenerOnInputField() {
        screen.inputText.addTextChangedListener {
            fitSizeOnInputField()
        }
    }

    private fun setClickOnBtnEqListener() {
        screen.btnEq.setOnClickListener {
            screen.outputText.visibility = View.VISIBLE
            screen.outputText.text = screen.inputText.text
        }
    }

    private fun setClickOnBtnBracketsListener() {
        listOf(
                screen.btnBracketOpen,
                screen.btnBracketClose
        ).forEach { button ->
            button.setOnClickListener {
                var text = ""
                when (it.tag) {
                    "bracketOpen" -> text = "("
                    "bracketClose" -> text = ")"
                }
                screen.inputText.text = screen.inputText.text.append(text)
            }
        }
    }

    private fun setClickOnBtnClearListener() {
        screen.btnClear.setOnClickListener {
            if (screen.inputText.text.isNotEmpty()) {
                screen.inputText.text.clear()
                screen.outputText.text.clear()
            }
        }
    }

    private fun setClickOnClearPrevBtnListener() {
        screen.btnClearPrev.setOnClickListener {
            if (screen.inputText.text.isNotEmpty()) {
                val inputTextLength = screen.inputText.text.length
                screen.inputText.setText(screen.inputText.text.substring(0, inputTextLength - 1))
            }
        }
    }

    private fun setClickOnNumbersListener() {

        listOf(
                screen.btnNum0,
                screen.btnNum1,
                screen.btnNum2,
                screen.btnNum3,
                screen.btnNum4,
                screen.btnNum5,
                screen.btnNum7,
                screen.btnNum8,
                screen.btnNum6,
                screen.btnNum9
        ).forEach {
            it.setOnClickListener(numBtnListener(it.tag.toString()))
        }
    }

    private fun setClickOnOperatorListener() {

        listOf(
                screen.btnDiv,
                screen.btnMul,
                screen.btnSub,
                screen.btnAdd
        ).forEach {
            it.setOnClickListener(operatorBtnListener(it.tag.toString()))
        }
    }

    private fun setClickOnSwitchSignListener() {
        screen.btnSwitchSign.setOnClickListener {
            val input = screen.inputText.text
            val inputTextLength = input.length

            if (input.isNotEmpty()) {
                if (isPreviousInput(inputTextLength, "(-")) {
                    deleteFromInput(start = inputTextLength - 2)
                } else {
                    val lastToken = getLastInputToken()
                    val lastTokenIndex = input.lastIndexOf(lastToken)
                    if (isNumber(lastToken)) {
                        if (isPreviousInput(lastTokenIndex, "(-")) {
                            deleteFromInput(lastTokenIndex - 2, lastTokenIndex)
                        } else {
                            addToInput(lastTokenIndex, "(-")
                        }
                    } else if (isOperator(lastToken)) {
                        addToInput(lastTokenIndex + lastToken.length, value = "(-")
                    } else if (lastToken == "(") {
                        addToInput(lastTokenIndex + lastToken.length, value = "(-")
                    } else if (lastToken == ")") {
                        addToInput(lastTokenIndex + lastToken.length, value = "*(-")
                    }
                }
            } else {
                addToInput(value = "(-")
            }
        }
    }

    private fun isNumber(str: String?) = str?.toDoubleOrNull()?.let { true } ?: false

    private fun addToInput(position: Int = 0, value: String): Boolean {
        val inputTextLength = screen.inputText.text.length
        if (kotlin.math.abs(position) <= inputTextLength) {

            val pivot = if (position >= 0) position else inputTextLength + position + 1

            screen.inputText.setText(
                    screen.inputText.text.substring(0, pivot)
                            .plus(value)
                            .plus(screen.inputText.text.substring(pivot))
            )
            return true
        }
        return false
    }

    private fun deleteFromInput(start: Int, end: Int = -1): Boolean {
        val inputTextLength = screen.inputText.text.length
        val end_ = if (end == -1) inputTextLength else end
        if (start > inputTextLength || end_ < 0) {
            return false
        }
        screen.inputText.text.replace(start, end_, "")
        return true
    }

    private fun isPreviousInput(index: Int, string: String): Boolean {
        val input = screen.inputText.text
        return try {
            input.substring(index - string.length, index) == string
        } catch (e: Exception) {
            false
        }
    }

    private fun numBtnListener(btnTag: String): View.OnClickListener {
        return View.OnClickListener {
            // если мы не ноль кликаем с самого начала
            if (btnTag != "0" || screen.inputText.text.isNotEmpty()) {
                if (getLastCharFromInput() == ")") {
                    screen.inputText.text.append("*")
                }
                screen.inputText.text = screen.inputText.text.append(btnTag)
            }
        }
    }

    private fun operatorBtnListener(btnTag: String): View.OnClickListener {
        return View.OnClickListener {
            var operator = ""
            when (btnTag) {
                "div" -> operator = Operator.DIV.value
                "mul" -> operator = Operator.MUL.value
                "sub" -> operator = Operator.SUB.value
                "add" -> operator = Operator.ADD.value
            }

            val lastToken = getLastInputToken()
            if (isOperator(lastToken)) {
                deleteFromInput(screen.inputText.text.length - 1)
                addToInput(screen.inputText.text.length, operator)
            } else if ((isNumber(lastToken) || lastToken == ")") || lastToken == "(" && operator == "-") {
                addToInput(-1, operator)
            }
        }
    }

    /**
     * checking if input is not like ** or //// or ---- etc
     * or if input is empty, we cant enter an operation sign
     */
    private fun isCorrectOperatorInput(operator: String): Boolean {
        val lastChar = getLastCharFromInput()
        if (lastChar.isNotEmpty() && !isOperator(lastChar)
            || lastChar == "(" && (operator == "-" || operator == "+")) {
            return true
        }
        return false
    }

    private fun getLastCharFromInput(): String {
        if (screen.inputText.text.isNotEmpty()) {
            return screen.inputText.text[screen.inputText.text.length - 1].toString()
        }
        return ""
    }

    private fun getLastInputToken(): String {
        val inputText = screen.inputText.text
        if (inputText.isNotEmpty()) {
            val lastChar = getLastCharFromInput()
            if (isOperator(lastChar)) {
                return lastChar
            } else if (lastChar == "(" || lastChar == ")") {
                return lastChar
            }
            return getLastInputNumber()
        }
        return ""
    }

    private fun getLastInputNumber(): String {

        var result = ""
        for (char_ in screen.inputText.text.reversed()) {
            if (!isOperator(char_.toString())
                    && char_.toString() != "("
                    && char_.toString() != ")") {
                result = result.plus(char_)
            } else {
                break
            }
        }
        return result.reversed()
    }

    /**
     * fits the size of inputText size depends on it fullness
     */
    private fun fitSizeOnInputField() {
        when (screen.inputText.text.length) {
            in 0..11 -> screen.inputText.textSize = LARGE_TEXT_SIZE
            in 12..22 -> screen.inputText.textSize = MEDIUM_TEXT_SIZE
            in 23..33 -> screen.inputText.textSize = SMALL_TEXT_SIZE
        }
    }

    private fun isOperator(char: String): Boolean {
        return Operator.values().any { it.value == char }
    }

}