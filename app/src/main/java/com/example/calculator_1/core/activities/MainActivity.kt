package com.example.calculator_1.core.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_1.BuildConfig
import com.example.calculator_1.core.base.controller.ClickController
import com.example.calculator_1.core.base.model.Model
import com.example.calculator_1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainScreen: ActivityMainBinding
    private lateinit var clickController: ClickController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainScreen = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.hide()  // hide program name on top side bar
        setContentView(mainScreen.root)

        miniTests()

        clickController = ClickController(mainScreen)
        clickController.setOnclickListeners()


    }


    private fun miniTests() {

        //testing model
        val isNum = listOf(
            Model.isNum("2"),
            Model.isNum("22.3"),
            Model.isNum("0.3"),
            Model.isNum("-14"),
            !Model.isNum("a")
        )

        val isOperator = listOf(
            Model.isOperator("+"),
            Model.isOperator("-"),
            Model.isOperator("*"),
            Model.isOperator("/"),
            !Model.isOperator("++"),
            !Model.isOperator("+a"),
            !Model.isOperator("2+"),
            !Model.isOperator("2")
        )

        val extractNumber = listOf(
            Model.extractNumber("(324+15-8)", 1) == Pair("324", 4),
            Model.extractNumber("324+15-8)", 0) == Pair("324", 3)
        )

        if (BuildConfig.DEBUG
            && !isNum.all { it }
            && !isOperator.all { it }
            && !extractNumber.all { it }
        ) {
            error("Assertion failed")
        }

        Model.setExpression("9+3-(2+8)")
        var result: Double = Model.calculate()

        return
    }
}