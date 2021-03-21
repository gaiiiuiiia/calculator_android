package com.example.calculator_1.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var mainScreen : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()  // hide program name on top side bar
        mainScreen = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreen.root)


    }
}