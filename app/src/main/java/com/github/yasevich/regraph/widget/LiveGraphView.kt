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
        isAntiAlias = true
        strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, GRAPH_LINE_WIDTH_SP, context.resources.displayMetrics)
        style = Paint.Style.STROKE
    }
    private val colors: MutableMap<String, Int> = mutableMapOf()

    private val xScale: Double
        get() = width.toDouble() / xTotal
    private val yScale: Double
        get() = height / yTotal

    private val xTotal: Int = GRAPH_FRAME_SIZE

    private var yTotal: Double = 10.0
    private var xShift: Double = 0.0

    private var graphs: List<Graph> = emptyList()

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
            updateInterval = 1000L * xTotal / w
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graphs.forEach { draw(it, canvas) }
    }

    fun show(graphs: List<Graph>) {
        this.graphs = graphs
        this.updates = 0.0
        initGraphColors()

        xShift = (graphs.map { it.getXShift() }.min() ?: xTotal.toDouble()) - xTotal
        yTotal = Math.ceil(graphs.map { it.points.last().y }.max() ?: Double.MAX_VALUE)
    }

    private fun initGraphColors() {
        graphs.forEach {
            if (!colors.containsKey(it.name)) {
                colors[it.name] = it.generateColor()
            }
        }
    }

    private fun drawFrame() {
        if (isAttachedToWindow) {
            invalidate()
            updates += updateInterval.toDouble() / 1000L
            postDelayed({ drawFrame() }, updateInterval)
        }
    }

    private fun draw(graph: Graph, canvas: Canvas) {
        path.reset()
        graph.drawLineOn(path)
        paint.color = colors[graph.name] ?: Color.BLACK
        canvas.drawPath(path, paint)
    }

    private fun Graph.getXShift(): Double {
        val index = points.size - 1 - if (points.size > 1) 1 else 0
        return points[index].x
    }

    private fun Graph.drawLineOn(path: Path) {
        val iterator = points.iterator()
        /*val drawablePoints = mutableListOf<PointF>()*/
        while (iterator.hasNext()) {
            val point = iterator.next()
            val px = ((point.x - xShift - updates) * xScale).toFloat()
            val py = (point.y * yScale).toFloat()

            if (path.isEmpty) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
                /*drawablePoints.add(PointF(px, py))
                if (drawablePoints.size == 3) {
                    path.cubicTo(
                            drawablePoints[0].x, drawablePoints[0].y,
                            drawablePoints[1].x, drawablePoints[1].y,
                            drawablePoints[2].x, drawablePoints[2].y
                    )
                    drawablePoints.clear()
                }*/
            }
        }

        /*if (drawablePoints.isNotEmpty()) {
            when (drawablePoints.size) {
                2 -> path.quadTo(drawablePoints[0].x, drawablePoints[0].y, drawablePoints[1].x, drawablePoints[1].y)
                1 -> path.lineTo(drawablePoints[0].x, drawablePoints[0].y)
            }
        }*/
    }

    private fun Graph.generateColor(): Int {
        val code = name.padStart(3, 'A')
        val r = code[2].colorCode()
        val g = code[1].colorCode()
        val b = code[0].colorCode()
        return Color.rgb(r, g, b)
    }

    private fun Char.colorCode(): Int = (this - 'A') * 9
}
