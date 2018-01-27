package com.github.yasevich.regraph.model

import java.math.BigDecimal

data class CurrencyRate(val currencyCode: String, val amount: BigDecimal, val timestamp: Long)
