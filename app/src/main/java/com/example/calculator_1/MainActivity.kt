package com.example.calculator_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var mainScreen : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainScreen = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainScreen.root)

    }
}