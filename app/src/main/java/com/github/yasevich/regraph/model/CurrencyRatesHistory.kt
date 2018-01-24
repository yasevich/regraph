package com.github.yasevich.regraph.model

import java.util.Deque
import java.util.LinkedList

data class CurrencyRatesHistory(private val maxSize: Int) {

    val graphs: List<Graph>
        get() {
            val graphs: MutableMap<String, MutableList<Point>> = mutableMapOf()
            deque.asSequence()
                    .map {
                        it.rates.associateBy {
                            it.currencyCode
                        }
                    }
                    .forEach {
                        it.forEach {
                            graphs.getOrPut(it.key, {mutableListOf() })
                                    .add(it.value.point)
                        }
                    }
            return graphs.map { Graph(it.key, it.value) }
        }

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
