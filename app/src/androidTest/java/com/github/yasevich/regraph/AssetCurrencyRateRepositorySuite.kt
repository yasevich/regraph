package com.github.yasevich.regraph

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AssetCurrencyRateRepositorySuite {

    private val repository: CurrencyRateRepository
        get() = AssetCurrencyRateRepository(InstrumentationRegistry.getTargetContext())

    @Test
    fun testCurrencies() {
        val expected = listOf("USD", "EUR", "GBP", "AUD")
        val actual = repository.getCurrencies().result
        assertEquals(expected, actual)
    }

    @Test
    fun testRates() {
        val repository = repository

        var timestamp = 0L
        var base = CurrencyRate("GBP", BigDecimal.ONE, timestamp)

        var expected = listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.6579"), timestamp),
                CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
        )
        var actual = repository.getRates(timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("GBP", BigDecimal.ONE, ++timestamp)
        expected = listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.6554"), timestamp),
                CurrencyRate("AUD", BigDecimal("2.6599"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4041"), timestamp)
        )
        actual = repository.getRates(timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrency() {
        val timestamp = 0L
        val base = CurrencyRate("EUR", BigDecimal.ONE, timestamp)
        val expected = listOf(
                base,
                CurrencyRate("GBP", BigDecimal("0.7111"), timestamp),
                CurrencyRate("USD", BigDecimal("1.1789"), timestamp),
                CurrencyRate("AUD", BigDecimal("1.9100"), timestamp)
        )
        val actual = repository.getRates("EUR", timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithCurrencies() {
        val repository = repository

        var timestamp = 0L
        var base = CurrencyRate("GBP", BigDecimal.ONE, timestamp)

        var expected = listOf(
                base,
                CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
        )
        var actual = repository.getRates(currencies = setOf("GBP", "AUD", "EUR"), timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("GBP", BigDecimal.ONE, ++timestamp)
        expected = listOf(
                base,
                CurrencyRate("AUD", BigDecimal("2.6599"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4041"), timestamp)
        )
        actual = repository.getRates(currencies = setOf("AUD", "EUR"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrencyAndCurrencies() {
        val repository = repository

        var timestamp = 0L
        var base = CurrencyRate("EUR", BigDecimal.ONE, timestamp)

        var expected = listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.1789"), timestamp)
        )
        var actual = repository.getRates("EUR", setOf("EUR", "USD"), timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("EUR", BigDecimal.ONE, ++timestamp)
        expected = listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.1790"), timestamp)
        )
        actual = repository.getRates("EUR", setOf("USD"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownBase() {
        assertEquals(AppError.INVALID_CURRENCY, repository.getRates("XXX").error)
    }

    @Test
    fun testRatesEmptyCurrencies() {
        val timestamp = 0L
        val expected = listOf(CurrencyRate("GBP", BigDecimal.ONE, timestamp))
        val actual = repository.getRates(currencies = setOf(), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownCurrencies() {
        val timestamp = 0L
        val expected = listOf(CurrencyRate("GBP", BigDecimal.ONE, timestamp))
        val actual = repository.getRates(currencies = setOf("XXX"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }
}
