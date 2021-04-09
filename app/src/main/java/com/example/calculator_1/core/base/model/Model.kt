package com.example.calculator_1.core.base.model

import com.example.calculator_1.core.base.util.ArrayListStack
import com.example.calculator_1.core.data.Operator
import java.lang.Exception
import java.util.*

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

        fun calculate() : Double {
            val operatorStack: ArrayListStack<String> = ArrayListStack()
            val numbersStack: ArrayListStack<String> = ArrayListStack()

            var index = 0
            while (index != this.expression.length) {

                val currentSymbol = this.expression[index].toString()

                // проверка на число/точку
                if ( isNum(currentSymbol ) || currentSymbol == "." ) {
                    val (number, index_) = extractNumber(this.expression, index)
                    numbersStack.push(number)
                    index = index_
                }

                // проверка на оператор
                else if ( isOperator(currentSymbol) ) {

                    try {
                        val operatorInStack = operatorStack.top()

                        if ( operatorInStack == "(" ) {
                            operatorStack.push(currentSymbol)
                            index++
                            continue
                        }

                        while ( this.orderOperations[currentSymbol] ?: error("sorry, Boss!")
                            <= this.orderOperations[operatorInStack] ?: error("sorry, Boss!") ) {

                            val eval = evaluateTwoNumbersFromStack(numbersStack, operatorStack.pop())
                            numbersStack.push(eval.toString())
                        }
                        operatorStack.push(currentSymbol)
                        index++
                    } catch (e: EmptyStackException) {
                        operatorStack.push(currentSymbol)
                        index++
                    }
                }

                // проверка на закрывающуюся скобку
                else if ( currentSymbol == ")" ) {

                    try {
                        while ( operatorStack.top() != "(" ) {
                            // сделать вычисление результата из двух чисел в стеке
                            val eval = evaluateTwoNumbersFromStack(numbersStack, operatorStack.pop())
                            numbersStack.push(eval.toString())
                        }
                        operatorStack.pop()
                        index++
                    } catch (e: EmptyStackException) {
                        index++
                    }

                }

                else if ( currentSymbol == "(" ) {
                    operatorStack.push(currentSymbol)
                    index++
                }
            }

            return if ( !operatorStack.isEmpty() && !numbersStack.isEmpty() )  {
                evaluateTwoNumbersFromStack(numbersStack, operatorStack.pop())!!
            } else if ( operatorStack.isEmpty() && !numbersStack.isEmpty() ) {
                numbersStack.pop().toDouble()
            } else {
                throw Exception("incorrect output")
            }

        }

        fun evaluateTwoNumbersFromStack(stack: ArrayListStack<String>, operation: String) : Double? {
            val num1 = stack.pop().toDouble()
            val num2 = stack.pop().toDouble()
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

            for (i in index until string.length) {
                if (!isNum(string[i].toString())) {
                    break
                }
                resultNumber = resultNumber.plus(string[i])
            }
            return Pair(resultNumber, index + 1)
        }
    }
}