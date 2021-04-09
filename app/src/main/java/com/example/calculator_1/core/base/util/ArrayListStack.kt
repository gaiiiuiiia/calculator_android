package com.example.calculator_1.core.base.util

import java.util.*
import kotlin.collections.ArrayList

class ArrayListStack<E> : Stack<E> {

    private var stack: ArrayList<E> = ArrayList()

    override fun push(element: E) {
        stack.add(element)
    }

    override fun pop(): E {
        val poppedElement = top()
        stack.remove(poppedElement)
        return poppedElement
    }

    override fun top(): E {
       if (stack.isEmpty()) {
            throw EmptyStackException()
        }
        return stack[stack.lastIndex]
    }

    override fun isEmpty(): Boolean {
        return stack.isEmpty()
    }

    override fun size(): Int {
        return stack.size
    }
}