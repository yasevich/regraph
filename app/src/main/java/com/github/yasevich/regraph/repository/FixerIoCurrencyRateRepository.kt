package com.github.yasevich.regraph.repository

import com.github.yasevich.regraph.RATES_HISTORY_SIZE
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.currentTimeSeconds
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.math.BigDecimal

class FixerIoCurrencyRateRepository : CurrencyRateRepository {

    private val backend: FixerIoBackend = Retrofit.Builder()
            .baseUrl("https://api.fixer.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FixerIoBackend::class.java)

    override fun getCurrencies(): RepositoryResponse<List<String>> {
        val response = getLatest()
        return when (response.status) {
            AppStatus.SUCCESS -> RepositoryResponse.success(
                    listOf(response.result.base) + response.result.rates.keys.toList())
            AppStatus.REFUSED -> RepositoryResponse.error(response.error)
        }
    }

    override fun getRates(
            baseCurrency: String?,
            currencies: Set<String>?,
            timestamp: Long?
    ) : RepositoryResponse<CurrencyRates> {

        val ts = currentTimeSeconds()
        val response = getLatest(baseCurrency, currencies)
        return when (response.status) {
            AppStatus.SUCCESS -> RepositoryResponse.success(response.result.toCurrencyRates(ts, currencies))
            AppStatus.REFUSED -> RepositoryResponse.error(response.error)
        }
    }

    override fun getHistory(
            baseCurrency: String?,
            currencies: Set<String>?,
            timestampRange: LongRange?
    ): RepositoryResponse<CurrencyRatesHistory> {
        return RepositoryResponse.success(CurrencyRatesHistory(RATES_HISTORY_SIZE))
    }

    private fun getLatest(
            baseCurrency: String? = null,
            currencies: Set<String>? = null
    ): RepositoryResponse<Latest> {
        return try {
            val body = backend.latest(baseCurrency, currencies?.joinToString(","))
                    .execute()
                    .body()
            if (body != null) RepositoryResponse.success(body) else RepositoryResponse.error(AppError.TECHNICAL_ERROR)
        } catch (exception: IOException) {
            RepositoryResponse.error(AppError.NO_INTERNET_CONNECTION)
        }
    }

    private fun Latest.toCurrencyRates(timestamp: Long, currencies: Set<String>? = null): CurrencyRates {
        val filtered = rates.filter { currencies?.contains(it.key) ?: true }
        return CurrencyRates(
                listOf(CurrencyRate(base, BigDecimal.ONE, timestamp)) +
                        filtered.map { CurrencyRate(it.key, BigDecimal(it.value), timestamp) },
                timestamp)
    }
}

private interface FixerIoBackend {
    @GET("latest")
    fun latest(
            @Query("base") currencyBase: String? = null,
            @Query("symbols") currencies: String? = null
    ): Call<Latest>
}

class Latest(val base: String, val rates: Map<String, String>)
