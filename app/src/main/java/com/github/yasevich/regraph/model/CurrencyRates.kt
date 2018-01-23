package com.github.yasevich.regraph.model

import java.math.BigDecimal

data class CurrencyRates(val rates: List<CurrencyRate> = emptyList(), val timestamp: Long) {

    fun rebase(currencyCode: String): CurrencyRates {
        if (rates.isEmpty() || rates.first().currencyCode == currencyCode) {
            return this
        }

        val base = rates.find { it.currencyCode == currencyCode }
                ?: throw IllegalArgumentException("no such currency code: $currencyCode")
        val result = mutableListOf(base.copy(amount = BigDecimal.ONE))
        rates.filter { it != base }
                .forEach { result.add(it.copy(amount = it.amount.divide(base.amount, BigDecimal.ROUND_HALF_UP))) }
        return copy(rates = result)
    }
}
