package com.github.yasevich.regraph.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.github.yasevich.regraph.GRAPH_FRAME_SIZE
import com.github.yasevich.regraph.GRAPH_LINE_WIDTH_SP
import com.github.yasevich.regraph.MIN_UPDATE_INTERVAL
import com.github.yasevich.regraph.util.ceilTo
import com.github.yasevich.regraph.util.floorTo

class LiveGraphView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attrs,
        defStyleAttr
) {

    private val path: Path = Path()
    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, GRAPH_LINE_WIDTH_SP, context.resources.displayMetrics)
        style = Paint.Style.STROKE
    }

    private val xScale: Double
        get() = width / xRange.toDouble()
    private val yScale: Double
        get() = height / yRange

    private val xRange: Int = GRAPH_FRAME_SIZE

    private var yRange: Double = 10.0

    private var xShift: Double = 0.0
    private var yShift: Double = 0.0

    private var graphs: List<LiveGraph> = emptyList()

    private var updates: Double = 0.0
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
        if (w > 0) {
            updateInterval = 1000L * xRange / w
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graphs.forEach { draw(it, canvas) }
    }

    fun show(graphs: List<LiveGraph>) {
        this.graphs = graphs
        this.updates = 0.0

        xShift = (graphs.map { it.getXShift() }.min() ?: xRange.toDouble()) - xRange

        calculateRestrictions(graphs.map { it.extremes })
    }

    private fun calculateRestrictions(extremes: List<LiveGraph.Extremes>) {
        var minY = Double.MAX_VALUE
        var maxY = - minY

        for (extreme in extremes) {
            if (maxY < extreme.maxY) {
                maxY = extreme.maxY
            }
            if (minY > extreme.minY) {
                minY = extreme.minY
            }
        }

        val m = 4.0
        val delta = maxY - minY
        yRange = ((1 + 2 / m) * delta).ceilTo(1)
        yShift = (minY - delta / m).floorTo(1)
    }

    private fun drawFrame() {
        if (isAttachedToWindow) {
            invalidate()
            updates += updateInterval.toDouble() / 1000L
            postDelayed({ drawFrame() }, updateInterval)
        }
    }

    private fun draw(graph: LiveGraph, canvas: Canvas) {
        path.reset()
        graph.drawLineOn(path)
        paint.color = graph.color
        canvas.drawPath(path, paint)
    }

    private fun LiveGraph.getXShift(): Double {
        val index = points.size - 1 - if (points.size > 1) 1 else 0
        return points[index].x
    }

    private fun LiveGraph.drawLineOn(path: Path) {
        val iterator = points.iterator()
        while (iterator.hasNext()) {
            val point = iterator.next()
            val px = ((point.x - xShift - updates) * xScale).toFloat()
            val py = (height - (point.y - yShift) * yScale).toFloat()

            if (path.isEmpty) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
    }
}
