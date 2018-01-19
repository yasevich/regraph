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

        xShift = (graphs.map { it.points.last().x }.max() ?: Double.MAX_VALUE) - xTotal
        yTotal = Math.ceil(graphs.map { it.points.last().y }.max() ?: Double.MAX_VALUE)
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

    private fun initGraphColors() {
        graphs.forEach {
            if (!colors.containsKey(it.name)) {
                colors[it.name] = it.generateColor()
            }
        }
    }

    private fun Graph.drawLineOn(path: Path) {
        points.forEach {
            val px = ((it.x - xShift - updates) * xScale).toFloat()
            val py = (it.y * yScale).toFloat()
            if (path.isEmpty) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
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
