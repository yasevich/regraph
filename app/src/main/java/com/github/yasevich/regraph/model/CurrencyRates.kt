package com.github.yasevich.regraph.model

import java.math.BigDecimal

data class CurrencyRates(val rates: List<CurrencyRate> = emptyList(), val timestamp: Long) {

    fun rebase(currencyCode: String): CurrencyRates {
        if (rates.isEmpty() || rates.first().currencyCode == currencyCode) {
            return this
        }

        val base = rates.find { it.currencyCode == currencyCode }
                ?: throw IllegalArgumentException("no such currency code: $currencyCode")

        val rebased = rates.toMutableList()
                .apply { remove(base) }
                .map { it.copy(amount = it.amount.divide(base.amount, base.amount.scale(), BigDecimal.ROUND_HALF_UP)) }

        return copy(rates = listOf(base.copy(amount = BigDecimal.ONE), *rebased.toTypedArray()))
    }
}
