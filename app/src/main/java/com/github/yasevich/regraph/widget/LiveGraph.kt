package com.github.yasevich.regraph.widget

data class LiveGraph(val name: String, val points: List<LiveGraphPoint>, val color: Int) {

    val extremes: Extremes by lazy {
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = -Double.MAX_VALUE
        var maxY = -Double.MAX_VALUE

        points.forEach {
            if (it.x < minX) {
                minX = it.x
            }
            if (it.y < minY) {
                minY = it.y
            }
            if (it.x > maxX) {
                maxX = it.x
            }
            if (it.y > maxY) {
                maxY = it.y
            }
        }

        Extremes(minX, minY, maxX, maxY)
    }

    data class Extremes(val minX: Double, val minY: Double, val maxX: Double, val maxY: Double)
}
