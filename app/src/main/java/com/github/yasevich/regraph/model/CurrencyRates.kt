package com.github.yasevich.regraph.model

import com.github.yasevich.regraph.RATES_HISTORY_SIZE
import java.util.Deque
import java.util.LinkedList

class CurrencyRates(val name: String) {

    private val rates: Deque<CurrencyRate> = LinkedList()

    fun add(rate: CurrencyRate) {
        if (name != rate.currency.currencyCode) {
            throw IllegalArgumentException("Trying to add $rate to a history records of $name")
        }
        rates.add(rate)
        if (rates.size > RATES_HISTORY_SIZE) {
            rates.removeFirst()
        }
    }

    fun toGraph(): Graph = Graph(name, rates.map { Point(it.timestamp.toFloat(), it.amount.toFloat()) })
}
