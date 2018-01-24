package com.github.yasevich.regraph

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository
import com.github.yasevich.regraph.repository.CurrencyRateRepository
import com.github.yasevich.regraph.util.currentTimeSeconds
import com.github.yasevich.regraph.util.secondsAtStartOfDay
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class AssetCurrencyRateRepositorySuite {

    private val repository: CurrencyRateRepository
        get() = AssetCurrencyRateRepository(InstrumentationRegistry.getTargetContext())

    private val timestamp: Long
        get() = secondsAtStartOfDay(currentTimeSeconds())

    @Test
    fun testCurrencies() {
        val expected = listOf("USD", "EUR", "GBP", "AUD")
        val actual = repository.getCurrencies().result
        assertEquals(expected, actual)
    }

    @Test
    fun testRates() {
        val repository = repository

        var timestamp = timestamp
        var base = CurrencyRate("GBP", BigDecimal.ONE, timestamp)

        var expected = CurrencyRates(listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.6579"), timestamp),
                CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
        ), timestamp)
        var actual = repository.getRates(timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("GBP", BigDecimal.ONE, ++timestamp)
        expected = CurrencyRates(listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.6554"), timestamp),
                CurrencyRate("AUD", BigDecimal("2.6599"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4041"), timestamp)
        ), timestamp)
        actual = repository.getRates(timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrency() {
        val timestamp = timestamp
        val base = CurrencyRate("EUR", BigDecimal.ONE, timestamp)
        val expected = CurrencyRates(listOf(
                base,
                CurrencyRate("GBP", BigDecimal("0.7111"), timestamp),
                CurrencyRate("USD", BigDecimal("1.1789"), timestamp),
                CurrencyRate("AUD", BigDecimal("1.9100"), timestamp)
        ), timestamp)
        val actual = repository.getRates("EUR", timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithCurrencies() {
        val repository = repository

        var timestamp = timestamp
        var base = CurrencyRate("GBP", BigDecimal.ONE, timestamp)

        var expected = CurrencyRates(listOf(
                base,
                CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
        ), timestamp)
        var actual = repository.getRates(currencies = setOf("GBP", "AUD", "EUR"), timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("GBP", BigDecimal.ONE, ++timestamp)
        expected = CurrencyRates(listOf(
                base,
                CurrencyRate("AUD", BigDecimal("2.6599"), timestamp),
                CurrencyRate("EUR", BigDecimal("1.4041"), timestamp)
        ), timestamp)
        actual = repository.getRates(currencies = setOf("AUD", "EUR"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrencyAndCurrencies() {
        val repository = repository

        var timestamp = timestamp
        var base = CurrencyRate("EUR", BigDecimal.ONE, timestamp)

        var expected = CurrencyRates(listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.1789"), timestamp)
        ), timestamp)
        var actual = repository.getRates("EUR", setOf("EUR", "USD"), timestamp = timestamp).result
        assertEquals(expected, actual)

        base = CurrencyRate("EUR", BigDecimal.ONE, ++timestamp)
        expected = CurrencyRates(listOf(
                base,
                CurrencyRate("USD", BigDecimal("1.1790"), timestamp)
        ), timestamp)
        actual = repository.getRates("EUR", setOf("USD"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownBase() {
        assertEquals(AppError.INVALID_CURRENCY, repository.getRates("XXX").error)
    }

    @Test
    fun testRatesEmptyCurrencies() {
        val timestamp = timestamp
        val expected = CurrencyRates(listOf(CurrencyRate("GBP", BigDecimal.ONE, timestamp)), timestamp)
        val actual = repository.getRates(currencies = setOf(), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownCurrencies() {
        val timestamp = timestamp
        val expected = CurrencyRates(listOf(CurrencyRate("GBP", BigDecimal.ONE, timestamp)), timestamp)
        val actual = repository.getRates(currencies = setOf("XXX"), timestamp = timestamp).result
        assertEquals(expected, actual)
    }

    @Test
    fun testHistory() {
        var timestamp = timestamp
        val range = timestamp .. timestamp + 1

        val expected = CurrencyRatesHistory(RATES_HISTORY_SIZE)
        with(expected) {
            add(CurrencyRates(listOf(
                    CurrencyRate("GBP", BigDecimal.ONE, timestamp),
                    CurrencyRate("USD", BigDecimal("1.6579"), timestamp),
                    CurrencyRate("AUD", BigDecimal("2.6860"), timestamp),
                    CurrencyRate("EUR", BigDecimal("1.4063"), timestamp)
            ), timestamp))
            add(CurrencyRates(listOf(
                    CurrencyRate("GBP", BigDecimal.ONE, ++timestamp),
                    CurrencyRate("USD", BigDecimal("1.6554"), timestamp),
                    CurrencyRate("AUD", BigDecimal("2.6599"), timestamp),
                    CurrencyRate("EUR", BigDecimal("1.4041"), timestamp)
            ), timestamp))
        }

        assertEquals(expected, repository.getHistory(timestampRange = range).result)
    }
}
