package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.util.currentTimeSeconds
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyRatesSuite {

    @Test
    fun testRebase() {
        val timestamp = currentTimeSeconds()

        val expected = CurrencyRates(listOf(
                CurrencyRate("EUR", BigDecimal.ONE, timestamp),
                CurrencyRate("GBP", BigDecimal("0.7111"), timestamp),
                CurrencyRate("USD", BigDecimal("1.1789"), timestamp),
                CurrencyRate("AUD", BigDecimal("1.9100"), timestamp)
        ), timestamp)

        val actual = CurrencyRates(listOf(
                CurrencyRate("GBP", BigDecimal.ONE, timestamp),
                CurrencyRate("USD", BigDecimal("1.6579"), timestamp),
                CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
        ), timestamp).rebase("EUR")

        assertEquals(expected, actual)
    }
}
