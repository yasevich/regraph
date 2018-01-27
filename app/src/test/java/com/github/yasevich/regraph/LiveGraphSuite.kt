package com.github.yasevich.regraph

import com.github.yasevich.regraph.widget.LiveGraph
import com.github.yasevich.regraph.widget.LiveGraphPoint
import junit.framework.Assert.assertEquals
import org.junit.Test

class LiveGraphSuite {

    @Test
    fun testExtremes() {
        val expected = LiveGraph.Extremes(-15.0, -14.0, 15.0, 14.0)
        val actual = LiveGraph("test", listOf(
                LiveGraphPoint(-10.0, 11.0),
                LiveGraphPoint(-15.0, 14.0),
                LiveGraphPoint(0.0, 0.0),
                LiveGraphPoint(10.0, -14.0),
                LiveGraphPoint(15.0, -11.0)
        )).extremes
        assertEquals(expected, actual)
    }
}