package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.CurrencyRate
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyRateSuite {

    @Test
    fun testPoint() {
        val timestamp = System.currentTimeMillis() / 1000
        val value1 = CurrencyRate("RUB", BigDecimal.ONE, timestamp)
        val value2 = CurrencyRate("RUB", BigDecimal.ONE, timestamp + 1)
        assertNotEquals(value1.point, value2.point)
    }
}
