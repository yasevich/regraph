package com.github.yasevich.regraph.view

import com.github.yasevich.regraph.util.ColorPicker

class CurrencyColorMapImpl(private val colorPicker: ColorPicker) : CurrencyColorMap {

    private val colorMap: MutableMap<String, Int> = mutableMapOf()

    override fun getColor(currency: String): Int = colorMap.getOrPut(currency, { colorPicker.nextColor() })
}
