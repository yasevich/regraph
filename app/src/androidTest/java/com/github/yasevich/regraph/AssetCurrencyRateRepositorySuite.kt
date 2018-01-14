package com.github.yasevich.regraph

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.CurrencyRateModel
import com.github.yasevich.regraph.repository.AssetCurrencyRateRepository
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

    @Test
    fun testCurrencies() {
        val expected = listOf("USD", "EUR", "GBP", "AUD")
        val actual = getRepository().getCurrencies().result
        assertEquals(expected, actual)
    }

    @Test
    fun testRates() {
        val repository = getRepository()
        val base = CurrencyRateModel("GBP", BigDecimal.ONE)

        var expected = listOf(
                base,
                CurrencyRateModel("USD", BigDecimal("1.6579"), base),
                CurrencyRateModel("AUD", BigDecimal("2.6860"), base),
                CurrencyRateModel("EUR", BigDecimal("1.4063"), base)
        )
        var actual = repository.getRates().result
        assertEquals(expected, actual)

        expected = listOf(
                base,
                CurrencyRateModel("USD", BigDecimal("1.6554"), base),
                CurrencyRateModel("AUD", BigDecimal("2.6599"), base),
                CurrencyRateModel("EUR", BigDecimal("1.4041"), base)
        )
        actual = repository.getRates().result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrency() {
        val base = CurrencyRateModel("EUR", BigDecimal.ONE)
        val expected = listOf(
                base,
                CurrencyRateModel("GBP", BigDecimal("0.7111"), base),
                CurrencyRateModel("USD", BigDecimal("1.1789"), base),
                CurrencyRateModel("AUD", BigDecimal("1.9100"), base)
        )
        val actual = getRepository().getRates("EUR").result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithCurrencies() {
        val repository = getRepository()
        val base = CurrencyRateModel("GBP", BigDecimal.ONE)

        var expected = listOf(
                base,
                CurrencyRateModel("AUD", BigDecimal("2.6860"), base),
                CurrencyRateModel("EUR", BigDecimal("1.4063"), base)
        )
        var actual = repository.getRates(currencies = setOf("GBP", "AUD", "EUR")).result
        assertEquals(expected, actual)

        expected = listOf(
                base,
                CurrencyRateModel("AUD", BigDecimal("2.6599"), base),
                CurrencyRateModel("EUR", BigDecimal("1.4041"), base)
        )
        actual = repository.getRates(currencies = setOf("AUD", "EUR")).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesWithBaseCurrencyAndCurrencies() {
        val repository = getRepository()
        val base = CurrencyRateModel("EUR", BigDecimal.ONE)

        var expected = listOf(
                base,
                CurrencyRateModel("USD", BigDecimal("1.1789"), base)
        )
        var actual = repository.getRates("EUR", setOf("EUR", "USD")).result
        assertEquals(expected, actual)

        expected = listOf(
                base,
                CurrencyRateModel("USD", BigDecimal("1.1790"), base)
        )
        actual = repository.getRates("EUR", setOf("USD")).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownBase() {
        assertEquals(AppError.INVALID_CURRENCY, getRepository().getRates("XXX").error)
    }

    @Test
    fun testRatesEmptyCurrencies() {
        val expected = listOf(CurrencyRateModel("GBP", BigDecimal.ONE))
        val actual = getRepository().getRates(currencies = setOf()).result
        assertEquals(expected, actual)
    }

    @Test
    fun testRatesUnknownCurrencies() {
        val expected = listOf(CurrencyRateModel("GBP", BigDecimal.ONE))
        val actual = getRepository().getRates(currencies = setOf("XXX")).result
        assertEquals(expected, actual)
    }

    private fun getRepository() = AssetCurrencyRateRepository(InstrumentationRegistry.getTargetContext())
}
