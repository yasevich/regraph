package com.github.yasevich.regraph.model

import java.math.BigDecimal
import java.util.Currency

data class CurrencyRateModel(val currency: Currency, val amount: BigDecimal, val base: CurrencyRateModel? = null) {
    constructor(currencyName: String, amount: BigDecimal, base: CurrencyRateModel? = null) :
            this(Currency.getInstance(currencyName), amount, base)
}
