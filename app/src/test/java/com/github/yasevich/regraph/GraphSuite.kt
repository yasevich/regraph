package com.github.yasevich.regraph

import com.github.yasevich.regraph.model.Graph
import com.github.yasevich.regraph.model.Point
import junit.framework.Assert.assertEquals
import org.junit.Test

class GraphSuite {

    @Test
    fun testExtremes() {
        val expected = Graph.Extremes(-15.0, -14.0, 15.0, 14.0)
        val actual = Graph("test", listOf(
                Point(-10.0,  11.0),
                Point(-15.0,  14.0),
                Point(  0.0,   0.0),
                Point( 10.0, -14.0),
                Point( 15.0, -11.0)
        )).extremes
        assertEquals(expected, actual)
    }
}