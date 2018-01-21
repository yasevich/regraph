package com.github.yasevich.regraph.model

import java.math.BigDecimal

data class CurrencyRate(val currencyCode: String, private val amount: BigDecimal, private val timestamp: Long) {
    val point: Point
        get() = Point(timestamp.toDouble(), amount.toDouble())
}
