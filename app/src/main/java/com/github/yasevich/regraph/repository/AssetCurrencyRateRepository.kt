package com.github.yasevich.regraph.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.yasevich.regraph.RATES_HISTORY_SIZE
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.SECONDS_IN_DAY
import com.github.yasevich.regraph.util.copyToInternalStorage
import com.github.yasevich.regraph.util.currentTimeSeconds
import com.github.yasevich.regraph.util.secondsAtStartOfDay
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

private const val FILE_NAME = "rates.sqlite"
private const val DEFAULT_BASE = "GBP"

private const val TABLE_CURRENCIES = "currencies"
private const val TABLE_RATES = "rates"

private const val COLUMN_CODE = "code"
private const val COLUMN_RATES = "rates"
private const val COLUMN_SECOND = "second"

class AssetCurrencyRateRepository(context: Context) : CurrencyRateRepository {

    private val database: SQLiteDatabase by lazy { openDatabase(context) }

    private val defaultTimestampRange: LongRange
        get() {
            val currentTimeSeconds = currentTimeSeconds()
            return currentTimeSeconds - RATES_HISTORY_SIZE .. currentTimeSeconds
        }

    override fun getCurrencies(): RepositoryResponse<List<String>> {
        try {
            database.query(
                    TABLE_CURRENCIES,
                    arrayOf(COLUMN_CODE),
                    null,
                    null,
                    null,
                    null,
                    null
            ).use {
                val result = mutableListOf<String>()
                while (it.moveToNext()) {
                    result.add(it.getString(0))
                }
                return RepositoryResponse.success(result)
            }
        } catch (e: Exception) {
            return RepositoryResponse.error(AppError.TECHNICAL_ERROR)
        }
    }

    override fun getRates(
            baseCurrency: String?,
            currencies: Set<String>?,
            timestamp: Long?
    ): RepositoryResponse<CurrencyRates> {

        val ts = timestamp ?: currentTimeSeconds()
        val day = secondsAtStartOfDay(ts)
        val rawRates = try {
            database.query(
                    TABLE_RATES,
                    arrayOf(COLUMN_RATES, COLUMN_SECOND),
                    "$COLUMN_SECOND = ?",
                    arrayOf("${ts % SECONDS_IN_DAY}"),
                    null,
                    null,
                    null,
                    "1"
            ).use {
                if (it.moveToNext()) {
                    it.getString(0) to day + it.getLong(1)
                } else {
                    val rate = CurrencyRate(baseCurrency ?: DEFAULT_BASE, BigDecimal.ONE, ts)
                    return RepositoryResponse.success(CurrencyRates(listOf(rate), ts))
                }
            }
        } catch (e: Exception) {
            return RepositoryResponse.error(AppError.TECHNICAL_ERROR)
        }

        val rates = filter(parse(rawRates.first), merge(baseCurrency, currencies))
        if (rates.isEmpty()) {
            return RepositoryResponse.success(CurrencyRates(timestamp = rawRates.second))
        }
        if (baseCurrency != null && !rates.containsKey(baseCurrency)) {
            return RepositoryResponse.error(AppError.INVALID_CURRENCY)
        }

        return RepositoryResponse.success(createList(rates.toMutableMap(), baseCurrency, rawRates.second))
    }

    override fun getHistory(
            baseCurrency: String?,
            currencies: Set<String>?,
            timestampRange: LongRange?
    ): RepositoryResponse<CurrencyRatesHistory> {

        val history = CurrencyRatesHistory(RATES_HISTORY_SIZE)
        (timestampRange ?: defaultTimestampRange)
                .map { getRates(baseCurrency, currencies, it) }
                .forEach {
                    when (it.status) {
                        AppStatus.SUCCESS -> history.add(it.result!!)
                        AppStatus.REFUSED -> return RepositoryResponse.error(it.error!!)
                    }
                }
        return RepositoryResponse.success(history)
    }

    private fun merge(baseCurrency: String?, currencies: Set<String>?): Set<String>? {
        return if (currencies != null) {
            currencies + (baseCurrency ?: DEFAULT_BASE)
        } else {
            currencies
        }
    }

    private fun openDatabase(context: Context): SQLiteDatabase {
        val outFileName = "${context.filesDir.absolutePath}/$FILE_NAME"
        if (!File(outFileName).exists()) {
            context.assets.copyToInternalStorage(FILE_NAME, outFileName)
        }
        return SQLiteDatabase.openDatabase(outFileName, null, SQLiteDatabase.OPEN_READONLY)
    }

    private fun parse(rawRates: String): Map<String, BigDecimal> {
        val map = mutableMapOf(DEFAULT_BASE to BigDecimal.ONE)
        with(JSONObject(rawRates)) {
            keys().forEach {
                map[it] = BigDecimal(getString(it))
            }
        }
        return map
    }

    private fun filter(rates: Map<String, BigDecimal>, currencies: Set<String>?): Map<String, BigDecimal> {
        return if (currencies != null) rates.filterKeys { currencies.contains(it) } else rates
    }

    private fun createList(rates: MutableMap<String, BigDecimal>, baseCurrency: String?, timestamp: Long):
            CurrencyRates {

        val baseCurrencyCode = baseCurrency ?: if (rates.containsKey(DEFAULT_BASE)) DEFAULT_BASE else rates.keys.first()
        val base = CurrencyRate(baseCurrencyCode, BigDecimal.ONE, timestamp)

        val baseRate = rates[baseCurrencyCode]!!
        val result = mutableListOf(base)

        with(rates) {
            remove(baseCurrencyCode)
            forEach {
                result.add(CurrencyRate(it.key, it.value.divide(baseRate, 4, RoundingMode.HALF_UP), timestamp))
            }
        }

        return CurrencyRates(result, timestamp)
    }
}
