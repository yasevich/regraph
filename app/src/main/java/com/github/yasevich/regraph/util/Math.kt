package com.github.yasevich.regraph.util

fun Double.ceilTo(step: Double): Double = Math.ceil(this / step) * step

fun Double.ceilTo(n: Int): Double = mathTo(n, Math::ceil)

fun Double.floorTo(n: Int): Double = mathTo(n, Math::floor)

private fun Double.mathTo(n: Int, func: (Double) -> Double): Double {
    val abs = Math.abs(this)
    val ref = Math.pow(10.0, n.toDouble())
    var multiplier = 1.0
    when {
        abs < ref -> {
            while (abs * multiplier * 10 < ref) {
                multiplier *= 10
            }
        }
        abs > ref -> {
            while (abs * multiplier > ref) {
                multiplier /= 10
            }
        }
        else -> return this
    }
    return func(this * multiplier) / multiplier
}
