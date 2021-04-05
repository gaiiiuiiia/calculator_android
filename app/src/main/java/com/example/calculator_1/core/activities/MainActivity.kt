package com.example.calculator_1.core.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_1.core.base.controller.ClickController
import com.example.calculator_1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainScreen: ActivityMainBinding
    private lateinit var clickController: ClickController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainScreen = ActivityMainBinding.inflate(layoutInflater)

        supportActionBar?.hide()  // hide program name on top side bar
        setContentView(mainScreen.root)

        clickController = ClickController(mainScreen)
        clickController.setOnclickListeners()

    }
}