package com.github.yasevich.regraph.util

import android.graphics.Color
import java.util.Random

class PaletteColorPicker : ColorPicker {

    private val random: Random = Random()
    private val availableColors: MutableList<Int> = palette.toMutableList()

    override fun nextColor(): Int = availableColors.removeAt(random.nextInt(availableColors.size))

    companion object {

        private val palette: Set<Int> = setOf(
                Color.parseColor("#F44336"),
                Color.parseColor("#E91E63"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#673AB7"),
                Color.parseColor("#3F51B5"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#00BCD4"),
                Color.parseColor("#009688"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#8BC34A"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#FFEB3B"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#795548"),
                Color.parseColor("#9E9E9E"),
                Color.parseColor("#607D8B")
        )

        val paletteSize: Int = palette.size
    }
}
