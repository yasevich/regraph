package com.github.yasevich.regraph.model

data class Graph(val name: String, val points: List<Point>) {

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
