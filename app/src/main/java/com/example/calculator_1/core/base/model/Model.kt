package com.example.calculator_1.core.base.model

import com.example.calculator_1.core.base.util.ArrayListStack
import com.example.calculator_1.core.data.Operator
import java.lang.Exception
import java.util.*
import kotlin.math.pow

class Model {

    companion object Companion {

        private var orderOperations: Map<String, Int> = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "^" to 3,
            "r" to 3    // square root
        )

        private var unaryOperators = listOf(
            Operator.SQRT.value
        )

        private lateinit var expression: String

        fun setExpression(expression: String) {
            this.expression = expression
        }

        fun calculate(): String {
            val operatorStack: ArrayListStack<String> = ArrayListStack()
            var numberStack: ArrayListStack<String> = ArrayListStack()

            var index = 0
            while (index != this.expression.length) {

                val currentSymbol = this.expression[index].toString()

                // проверка на число/точку
                if (isNum(currentSymbol) || currentSymbol == ".") {
                    val (number, index_) = extractNumber(this.expression, index)
                    numberStack.push(number)
                    index = index_
                }

                // проверка на оператор
                else if (isOperator(currentSymbol)) {

                    if (index + 1 < this.expression.length) {
                        if (currentSymbol == "-" && index != 0
                            && this.expression[index - 1].toString() == "(")
                        {
                            val (number, index_) = extractNumber(this.expression, index + 1)
                            numberStack.push("-".plus(number))
                            index = index_
                            continue
                        }
                    }

                    try {

                        while (true) {

                            val operatorInStack = operatorStack.top()

                            if (operatorInStack == "(" || operatorInStack == ")") {
                                break
                            }

                            if (this.orderOperations[currentSymbol] ?: error("sorry, Boss!")
                                <= this.orderOperations[operatorInStack] ?: error("sorry, Boss!")
                            ) {
                                val result = if (isUnaryOperator(operatorInStack)) {
                                    evaluateUnary(numberStack.pop(), operatorStack.pop())
                                } else {
                                    evaluateTwoNumbersFromStack(numberStack, operatorStack.pop())
                                }
                                numberStack.push(result.toString())
                            } else {
                                break
                            }
                        }
                        operatorStack.push(currentSymbol)
                        index++

                    } catch (e: EmptyStackException) {
                        operatorStack.push(currentSymbol)
                        index++
                    }
                }

                // проверка на закрывающуюся скобку
                else if (currentSymbol == ")") {

                    try {
                        while (operatorStack.top() != "(") {
                            val result = if (isUnaryOperator(operatorStack.top())) {
                                evaluateUnary(numberStack.pop(), operatorStack.pop())
                            } else {
                                evaluateTwoNumbersFromStack(numberStack, operatorStack.pop())
                            }
                            numberStack.push(result.toString())
                        }
                        operatorStack.pop()
                        index++
                    } catch (e: EmptyStackException) {
                        index++
                    }

                } else if (currentSymbol == "(") {
                    operatorStack.push(currentSymbol)
                    index++
                }
                // введено что-то не понятное - выходим из цикла
                else {
                    break;
                }
            }

            return if (!operatorStack.isEmpty() && !numberStack.isEmpty()) {
                numberStack = collectResultFromStacks(numberStack, operatorStack)
                clearNumber(numberStack.pop())
            } else if (operatorStack.isEmpty() && !numberStack.isEmpty()) {
                clearNumber(numberStack.pop())
            } else {
                throw Exception("incorrect output")
            }

        }

        fun clearNumber(number: String): String {
            return number.replace(Regex("\\.0*$"), "")
        }

        fun evaluateTwoNumbersFromStack(
            numberStack: ArrayListStack<String>,
            operation: String
        ): Double? {
            val num1 = numberStack.pop().toDouble()
            val num2 = numberStack.pop().toDouble()
            var result: Double? = null

            // порядок следования чисел в методах должен быть num2, num1 !
            when (operation) {
                Operator.ADD.value -> result = addt(num2, num1)
                Operator.SUB.value -> result = subtr(num2, num1)
                Operator.MUL.value -> result = mltpl(num2, num1)
                Operator.DIV.value -> result = dvsn(num2, num1)
                Operator.POW.value -> result = powr(num2, num1)
            }

            return result
        }

        fun evaluateUnary(number: String, operator: String): Double? {
            when (operator) {
                Operator.SQRT.value -> return powr(number.toDouble(), 0.5)
            }
            return null
        }

        /**
         * подсчет результата со стеков
         */
        fun collectResultFromStacks(
            numberStack: ArrayListStack<String>,
            operatorStack: ArrayListStack<String>
        ): ArrayListStack<String> {
            try {
                while (operatorStack.top().isNotEmpty()) {
                    val currentOperator = operatorStack.pop()

                    val result = if (isUnaryOperator(currentOperator)) {
                        evaluateUnary(numberStack.pop(), currentOperator)
                    } else {
                        evaluateTwoNumbersFromStack(numberStack, currentOperator)
                    }
                    numberStack.push(result.toString())
                }
            } catch (e: EmptyStackException) {
            }
            return numberStack
        }

        fun addt(num1: Double, num2: Double): Double = num1 + num2
        fun subtr(num1: Double, num2: Double): Double = num1 - num2
        fun mltpl(num1: Double, num2: Double): Double = num1 * num2
        fun dvsn(num1: Double, num2: Double): Double = num1 / num2
        fun powr(num1: Double, num2: Double): Double = num1.pow(num2)

        fun isOperator(elem: String): Boolean {
            return Operator.values().any { it.value == elem }
        }

        private fun isUnaryOperator(elem: String): Boolean {
            return unaryOperators.any { it == elem }
        }

        fun isNum(elem: String): Boolean {
            return elem.matches(Regex("^-?\\d+(.\\d)?$"))
        }

        /**
         *метод принимает на вход строку и идекс. извлекает из строки число,
         * начиная с индекса и возвращает извлеченное число и следующий после числа индекс
         */
        fun extractNumber(string: String, index: Int): Pair<String, Int> {
            var resultNumber = ""
            var lastIndex = 0

            for (i in index until string.length) {
                if ( !isNum(string[i].toString()) && string[i].toString() != "." ) {
                    break
                }
                resultNumber = resultNumber.plus(string[i])
                lastIndex = i
            }
            return Pair(resultNumber, lastIndex + 1)
        }
    }
}