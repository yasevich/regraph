package com.github.yasevich.regraph.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import java.util.Arrays

private const val MIN_UPDATE_INTERVAL: Long = 1000 / 60 // default: ~60 times per second

class LiveGraphView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attrs,
        defStyleAttr
) {

    private val points: MutableList<FloatArray> = mutableListOf()

    private val xSpeed: Long = 1000
    private val xScale: Int = 60

    private var yScale: Int = 10

    private var updateInterval: Long = MIN_UPDATE_INTERVAL
        set(value) {
            field = if (value < MIN_UPDATE_INTERVAL) MIN_UPDATE_INTERVAL else value
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        drawFrame()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateInterval = xSpeed * xScale / w
    }

    fun addPoints(points: FloatArray) {
        with(this.points) {
            if (isNotEmpty() && first().size != points.size) {
                clear()
            }
            add(points)
        }

        val max = points.max()
        if (max == null || max == Float.NaN) {
            throw IllegalArgumentException("${Arrays.toString(points)} contains illegal value.")
        }
        yScale = Math.ceil(max.toDouble()).toInt()
    }

    private fun drawFrame() {
        if (isAttachedToWindow) {
            invalidate()
            postDelayed({ drawFrame() }, updateInterval)
        }
    }
}
