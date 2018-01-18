package com.github.yasevich.regraph.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.util.copyToInternalStorage
import com.github.yasevich.regraph.util.currentTimeSeconds
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

private const val FILE_NAME = "rates.sqlite"
private const val DEFAULT_BASE = "GBP"

class AssetCurrencyRateRepository(context: Context) : CurrencyRateRepository {

    private val database: SQLiteDatabase by lazy { openDatabase(context) }

    override fun getCurrencies(): RepositoryResponse<List<String>> {
        try {
            database.query(
                    "currencies",
                    arrayOf("code"),
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

    override fun getRates(baseCurrency: String?, currencies: Set<String>?):
            RepositoryResponse<List<CurrencyRate>> {
        return select(baseCurrency, merge(baseCurrency, currencies))
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

    private fun select(baseCurrency: String?, currencies: Set<String>?): RepositoryResponse<List<CurrencyRate>> {
        val timestamp = currentTimeSeconds()
        val rawRates: String
        try {
            rawRates = database.query(
                    "rates",
                    arrayOf("rates"),
                    "second = ?",
                    arrayOf("${timestamp % 86400}"),
                    null,
                    null,
                    null,
                    "1"
            ).use {
                if (it.moveToNext()) {
                    it.getString(0)
                } else {
                    return RepositoryResponse.success(emptyList())
                }
            }
        } catch (e: Exception) {
            return RepositoryResponse.error(AppError.TECHNICAL_ERROR)
        }
        return createList(filter(parse(rawRates), currencies).toMutableMap(), baseCurrency, timestamp)
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
            RepositoryResponse<List<CurrencyRate>> {

        if (rates.isEmpty()) {
            return RepositoryResponse.success(emptyList())
        }

        if (baseCurrency != null && !rates.containsKey(baseCurrency)) {
            return RepositoryResponse.error(AppError.INVALID_CURRENCY)
        }

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

        return RepositoryResponse.success(result)
    }
}
