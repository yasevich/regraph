package com.github.yasevich.regraph.presenter

import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.widget.LiveGraph
import com.github.yasevich.regraph.widget.LiveGraphPoint

fun CurrencyRate.point(): LiveGraphPoint = LiveGraphPoint(timestamp.toDouble(), amount.toDouble())

fun CurrencyRatesHistory.graphs(): List<LiveGraph> {
    val graphs: MutableMap<String, MutableList<LiveGraphPoint>> = mutableMapOf()
    items.asSequence()
            .map {
                it.rates.associateBy {
                    it.currencyCode
                }
            }
            .forEach {
                it.forEach {
                    graphs.getOrPut(it.key, {mutableListOf() })
                            .add(it.value.point())
                }
            }
    return graphs.map { LiveGraph(it.key, it.value) }
}
