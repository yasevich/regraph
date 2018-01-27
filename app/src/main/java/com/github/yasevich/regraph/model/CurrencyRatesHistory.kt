package com.github.yasevich.regraph.model

import java.util.Deque
import java.util.LinkedList

data class CurrencyRatesHistory(private val maxSize: Int) {

    val items: List<CurrencyRates>
        get() = _items.toList()

    private val _items: Deque<CurrencyRates> = LinkedList()

    private val lock: Any = Any()

    fun add(rates: CurrencyRates) {
        with(_items) {
            synchronized(lock) {
                add(rates)
                while (size > maxSize) {
                    removeFirst()
                }
            }
        }
    }

    fun rebase(currencyCode: String) {
        with(_items) {
            synchronized(lock) {
                val newHistory = map { it.rebase(currencyCode) }
                clear()
                addAll(newHistory)
            }
        }
    }
}
