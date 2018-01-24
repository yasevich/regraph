package com.github.yasevich.regraph.repository

import com.github.yasevich.regraph.model.CurrencyRates
import com.github.yasevich.regraph.model.CurrencyRatesHistory

/**
 * Interface of a repository containing currency rates.
 */
interface CurrencyRateRepository {

    /**
     * @return response, containing a set of all available currencies that could be requested from this repository
     */
    fun getCurrencies(): RepositoryResponse<List<String>>

    /**
     * Gets rates from this repository. Repository must return a list of currency rates with base currency set to
     * [baseCurrency] if specified, otherwise default currency is set as a base currency. Repository must return rates
     * for [currencies] if specified (including [baseCurrency] if set), otherwise it must return rates for all available
     * currencies.
     *
     * @param baseCurrency base currency code
     * @param currencies filter result to include only this currencies excluding or including [baseCurrency]
     * @return response, containing list of currency rates for specified filters
     */
    fun getRates(baseCurrency: String? = null, currencies: Set<String>? = null, timestamp: Long? = null):
            RepositoryResponse<CurrencyRates>

    /**
     * Like [getRates] but returns currency rates history for specified range
     */
    fun getHistory(baseCurrency: String? = null, currencies: Set<String>? = null, timestampRange: LongRange? = null):
            RepositoryResponse<CurrencyRatesHistory>
}
