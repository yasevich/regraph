package com.github.yasevich.regraph.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.github.yasevich.regraph.GRAPH_FRAME_SIZE
import com.github.yasevich.regraph.UPDATES_PER_SECOND
import com.github.yasevich.regraph.model.Graph

class LiveGraphView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(
        context,
        attrs,
        defStyleAttr
) {

    private val xSpeed: Long = 1000L
    private val xScale: Int = GRAPH_FRAME_SIZE

    private val path: Path = Path()
    private val paint: Paint = Paint()

    private var graphs: List<Graph> = emptyList()

    private var xPoints: FloatArray = FloatArray(0)

    private var yScale: Int = 10

    private var skip: Float = 1f

    private var updateInterval: Long = UPDATES_PER_SECOND
        set(value) {
            field = if (value < UPDATES_PER_SECOND) UPDATES_PER_SECOND else value
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        drawFrame()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0) {
            updateInterval = xSpeed * xScale / w
            skip = xScale.toFloat() / w
            xPoints = calculateXPoints(w)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graphs.forEach { draw(it, canvas) }
    }

    fun show(graphs: List<Graph>) {
        this.graphs = graphs

        val max = graphs.map { it.points.last().y }.max()
        if (max == null || max == Float.NaN) {
            throw IllegalArgumentException("Graphs contain illegal value.")
        }
        yScale = Math.ceil(max.toDouble()).toInt()
    }

    private fun drawFrame() {
        if (isAttachedToWindow) {
            invalidate()
            postDelayed({ drawFrame() }, updateInterval)
        }
    }

    private fun calculateXPoints(w: Int): FloatArray {
        val distance = w.toFloat() / (xScale - 1)
        return if (distance <= 1) {
            FloatArray(w, { it.toFloat() })
        } else {
            var currentPoint = 0f
            FloatArray(xScale, { currentPoint.also { currentPoint += distance } })
        }
    }

    private fun draw(graph: Graph, canvas: Canvas) {
        path.reset()
        // TODO draw graph here
        canvas.drawPath(path, paint)
    }
}
