package com.example.calculator_1.core.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.example.calculator_1.core.base.controller.ClickController
import com.example.calculator_1.core.base.model.Model
import com.example.calculator_1.databinding.ActivityMainBinding
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var mainScreen: ActivityMainBinding
    private lateinit var clickController: ClickController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainScreen = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.hide()  // hide program name on top side bar
        setContentView(mainScreen.root)

        try {
            miniTests()
        } catch (e: Exception) {
            print("BAD TESTS")
            exitProcess(0)
        }

        clickController = ClickController(mainScreen)
        clickController.setOnclickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("inputString", mainScreen.inputText.text.toString())
        outState.putString("outputString", mainScreen.outputText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainScreen.inputText.setText(savedInstanceState.getString("inputString"))
        mainScreen.outputText.setText(savedInstanceState.getString("outputString"))
    }

    private fun miniTests() {

        //testing model
        val testIsNum = fun(): Boolean {
            return listOf(
                Model.isNum("2"),
                Model.isNum("22.3"),
                Model.isNum("0.3"),
                Model.isNum("-14"),
                !Model.isNum("a")
            ).all { it }
        }

        val testIsOperator = fun(): Boolean {
            return listOf(
                Model.isOperator("+"),
                Model.isOperator("-"),
                Model.isOperator("*"),
                Model.isOperator("/"),
                !Model.isOperator("++"),
                !Model.isOperator("+a"),
                !Model.isOperator("2+"),
                !Model.isOperator("2")
            ).all { it }
        }

        val testExtractNumber = fun(): Boolean {
            return listOf(
                Model.extractNumber("(324+15-8)", 1) == Pair("324", 4),
                Model.extractNumber("324+15-8)", 0) == Pair("324", 3)
            ).all { it }
        }

        val testClearNumber = fun(): Boolean {
            return listOf(
                Model.clearNumber("3.1") == "3.1",
                Model.clearNumber("-5.1") == "-5.1",
                Model.clearNumber("0.0") == "0",
                Model.clearNumber("15.0") == "15",
                Model.clearNumber("-15.0") == "-15"
            ).all { it }
        }

        val testCalculate = fun(): Boolean {

            val testResult = mutableListOf<Boolean>()

            Model.setExpression("9+3-(2+8)")
            testResult.add(Model.calculate() == "2")

            Model.setExpression("54-6")
            testResult.add(Model.calculate() == "48")

            Model.setExpression("(-3)-5")
            testResult.add(Model.calculate() == "-8")

            Model.setExpression("2*15")
            testResult.add(Model.calculate() == "30")

            Model.setExpression("2*2-2")
            testResult.add(Model.calculate() == "2")

            Model.setExpression("2+5*3-8*9-6+5-2*2")
            testResult.add(Model.calculate() == "-60")

            Model.setExpression("2+5*(3-8)*9-6+5-2*2")
            testResult.add(Model.calculate() == "-228")

            Model.setExpression("2+5*((3-8)*9-6)+5-2*2")
            testResult.add(Model.calculate() == "-252")

            Model.setExpression("2.0+5*((3-8)*9-6)+5-2*2")
            testResult.add(Model.calculate() == "-252")

            Model.setExpression("2+5.00*((3-8)*9-6)+5-2*2.0")
            testResult.add(Model.calculate() == "-252")

            Model.setExpression("2.9+5*((3-8.0)*9-6)+5-2*2")
            testResult.add(Model.calculate() == "-251.1")

            Model.setExpression("2+5*((3.1-8.1)*9-6)+5-2*2")
            testResult.add(Model.calculate() == "-252")

            Model.setExpression("20^(2)")
            testResult.add(Model.calculate() == "400")

            Model.setExpression("0^(0)")
            testResult.add(Model.calculate() == "1")

            Model.setExpression("1^(0)")
            testResult.add(Model.calculate() == "1")

            Model.setExpression("(-3)^(2)")
            testResult.add(Model.calculate() == "9")

            Model.setExpression("(-3)^(3)")
            testResult.add(Model.calculate() == "-27")

            Model.setExpression("1/(-4)")
            testResult.add(Model.calculate() == "-0.25")

            Model.setExpression("1/5^2")
            testResult.add(Model.calculate() == "0.04")

            Model.setExpression("1/100")
            testResult.add(Model.calculate() == "0.01")

            Model.setExpression("r(4)")
            testResult.add(Model.calculate() == "2")

            Model.setExpression("r(4)+r(9)")
            testResult.add(Model.calculate() == "5")

            Model.setExpression("r(4+5)+r(9+7)")
            testResult.add(Model.calculate() == "7")

            Model.setExpression("r(r(81))")
            testResult.add(Model.calculate() == "3")

            return testResult.all { it }
        }

        val results = listOf(
            testIsNum(),
            testIsOperator(),
            testExtractNumber(),
            testClearNumber(),
            testCalculate()
        )

        if (results.any { !it }) {
            throw Exception("bad model tests")
        }

        return
    }
}