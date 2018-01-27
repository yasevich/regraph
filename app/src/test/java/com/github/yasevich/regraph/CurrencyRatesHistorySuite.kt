package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.presenter.graphs
import com.github.yasevich.regraph.util.currentTimeSeconds
import com.github.yasevich.regraph.widget.LiveGraph
import com.github.yasevich.regraph.widget.LiveGraphPoint
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyRatesHistorySuite {

    @Test
    fun testGraph() {
        val history = CurrencyRatesHistory(2)
        val timestamp = currentTimeSeconds()

        with(history) {
            add(CurrencyRates(listOf(
                    CurrencyRate("GBP", BigDecimal.ONE, timestamp),
                    CurrencyRate("USD", BigDecimal("1.6579"), timestamp),
                    CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                    CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
            ), timestamp))
            add(CurrencyRates(listOf(
                    CurrencyRate("GBP", BigDecimal.ONE, timestamp + 1),
                    CurrencyRate("USD", BigDecimal("1.6554"), timestamp + 1),
                    CurrencyRate("AUD", BigDecimal("2.6599"), timestamp + 1),
                    CurrencyRate("EUR", BigDecimal("1.4041"), timestamp + 1)
            ), timestamp))
        }

        val actual = history.graphs()
        val expected = listOf(
                LiveGraph("GBP", listOf(
                        LiveGraphPoint(timestamp.toDouble(), BigDecimal.ONE.toDouble()),
                        LiveGraphPoint((timestamp + 1).toDouble(), BigDecimal.ONE.toDouble())
                )),
                LiveGraph("USD", listOf(
                        LiveGraphPoint(timestamp.toDouble(), BigDecimal("1.6579").toDouble()),
                        LiveGraphPoint((timestamp + 1).toDouble(), BigDecimal("1.6554").toDouble())
                )),
                LiveGraph("AUD", listOf(
                        LiveGraphPoint(timestamp.toDouble(), BigDecimal("2.6860").toDouble()),
                        LiveGraphPoint((timestamp + 1).toDouble(), BigDecimal("2.6599").toDouble())
                )),
                LiveGraph("EUR", listOf(
                        LiveGraphPoint(timestamp.toDouble(), BigDecimal("1.4063").toDouble()),
                        LiveGraphPoint((timestamp + 1).toDouble(), BigDecimal("1.4041").toDouble())
                ))
        )

        assertEquals(expected, actual)
    }
}
