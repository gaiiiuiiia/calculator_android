package com.example.calculator_1.core.base.util

interface Stack<E> {

    fun push(element: E)

    fun pop(): E

    fun top(): E

    fun isEmpty(): Boolean

    fun size(): Int

}