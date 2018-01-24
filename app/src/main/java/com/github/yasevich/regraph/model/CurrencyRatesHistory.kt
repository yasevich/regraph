package com.github.yasevich.regraph.model

import java.util.Deque
import java.util.LinkedList

data class CurrencyRatesHistory(private val maxSize: Int) {

    private val deque: Deque<CurrencyRates> = LinkedList()

    fun add(rates: CurrencyRates) {
        with(deque) {
            add(rates)
            while (size > maxSize) {
                removeFirst()
            }
        }
    }
}
