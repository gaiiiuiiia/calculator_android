package com.example.calculator_1.core.base.model

import com.example.calculator_1.core.base.util.ArrayListStack
import com.example.calculator_1.core.data.Operator
import java.lang.Exception
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class Model {

    companion object Companion {

        private var orderOperations: Map<String, Int> = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "^" to 3
        )

        private lateinit var expression: String

        fun setExpression(expression: String) {
            this.expression = expression
        }

        fun calculate(): String {
            val operatorStack: ArrayListStack<String> = ArrayListStack()
            var numbersStack: ArrayListStack<String> = ArrayListStack()

            var index = 0
            while (index != this.expression.length) {

                val currentSymbol = this.expression[index].toString()

                // проверка на число/точку
                if (isNum(currentSymbol) || currentSymbol == ".") {
                    val (number, index_) = extractNumber(this.expression, index)
                    numbersStack.push(number)
                    index = index_
                }

                // проверка на оператор
                else if (isOperator(currentSymbol)) {

                    if (index + 1 < this.expression.length) {
                        if (currentSymbol == "-" && index != 0 && this.expression[index - 1].toString() == "(") {
                            val (number, index_) = extractNumber(this.expression, index + 1)
                            numbersStack.push("-".plus(number))
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
                                val eval =
                                    evaluateTwoNumbersFromStack(numbersStack, operatorStack.pop())
                                numbersStack.push(eval.toString())
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
                            // сделать вычисление результата из двух чисел в стеке
                            val eval =
                                evaluateTwoNumbersFromStack(numbersStack, operatorStack.pop())
                            numbersStack.push(eval.toString())
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
            }

            return if (!operatorStack.isEmpty() && !numbersStack.isEmpty()) {
                numbersStack = collectResultFromStacks(numbersStack, operatorStack)
                clearNumber(numbersStack.pop())
            } else if (operatorStack.isEmpty() && !numbersStack.isEmpty()) {
                clearNumber(numbersStack.pop())
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
            }

            return result
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
                    val res = evaluateTwoNumbersFromStack(numberStack, operatorStack.pop())
                    numberStack.push(res.toString())
                }
            } catch (e: EmptyStackException) {
            }
            return numberStack
        }

        fun addt(num1: Double, num2: Double): Double = num1 + num2
        fun subtr(num1: Double, num2: Double): Double = num1 - num2
        fun mltpl(num1: Double, num2: Double): Double = num1 * num2
        fun dvsn(num1: Double, num2: Double): Double = num1 / num2

        fun isOperator(elem: String): Boolean {
            return Operator.values().any { it.value == elem }
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