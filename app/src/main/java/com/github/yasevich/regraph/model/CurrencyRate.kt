package com.github.yasevich.regraph.model

import java.math.BigDecimal
import java.util.Currency

data class CurrencyRate(val currency: Currency, val amount: BigDecimal, val base: CurrencyRate? = null) {
    constructor(currencyName: String, amount: BigDecimal, base: CurrencyRate? = null) :
            this(Currency.getInstance(currencyName), amount, base)
}
