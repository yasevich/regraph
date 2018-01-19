package com.github.yasevich.regraph.model

import java.math.BigDecimal
import java.util.Currency

data class CurrencyRate(val currency: Currency, val amount: BigDecimal, val timestamp: Long) {

    val point: Point
        get() = Point(timestamp.toDouble(), amount.toDouble())

    constructor(currencyName: String, amount: BigDecimal, timestamp: Long) :
            this(Currency.getInstance(currencyName), amount, timestamp)
}
