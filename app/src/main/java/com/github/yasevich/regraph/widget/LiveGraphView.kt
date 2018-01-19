package com.github.yasevich.regraph.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
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

    private val path: Path = Path()
    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val xSpeed: Int = 1
    private val xTotal: Int = GRAPH_FRAME_SIZE

    private val xScale: Float
        get() = width.toFloat() / xTotal
    private val yScale: Float
        get() = height.toFloat() / yTotal

    private var xShift: Float = 0f
    private var yTotal: Float = 10f

    private var graphs: List<Graph> = emptyList()

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
            updateInterval = 1000L * xSpeed * xTotal / w
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graphs.forEach { draw(it, canvas) }
    }

    fun show(graphs: List<Graph>) {
        this.graphs = graphs
        xShift = ((graphs.map { it.points.first().x }.max() ?: Double.MAX_VALUE) - xTotal).toFloat()
        yTotal = Math.ceil(graphs.map { it.points.last().y }.max() ?: Double.MAX_VALUE).toFloat()
    }

    private fun drawFrame() {
        if (isAttachedToWindow) {
            invalidate()
            postDelayed({ drawFrame() }, updateInterval)
        }
    }

    private fun draw(graph: Graph, canvas: Canvas) {
        path.reset()
        graph.drawLineOn(path)
        canvas.drawPath(path, paint)
    }

    private fun Graph.drawLineOn(path: Path) {
        points.forEach {
            if (path.isEmpty) {
                path.moveTo((it.x - xShift).toFloat(), it.y.toFloat())
            } else {
                path.lineTo((it.x - xShift).toFloat(), it.y.toFloat())
            }
        }

        with(Matrix()) {
            setScale(xScale, yScale)
            path.transform(this)
        }
    }
}
