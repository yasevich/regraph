package com.github.yasevich.regraph

import com.github.yasevich.regraph.util.ceilTo
import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA_DOUBLE = 0.00001

class MathSuite {

    @Test
    fun testDoubleCeil() {
        assertEquals(1.0, 0.5.ceilTo(1.0), DELTA_DOUBLE)
        assertEquals(1.0, 1.0.ceilTo(1.0), DELTA_DOUBLE)
        assertEquals(0.2, 0.1.ceilTo(0.2), DELTA_DOUBLE)
        assertEquals(4.0, 3.5.ceilTo(2.0), DELTA_DOUBLE)
    }

    @Test
    fun testRoundTo() {
        assertEquals(12.35, 12.3411.ceilTo(4), DELTA_DOUBLE)
        assertEquals(1235.0, 1234.11.ceilTo(4), DELTA_DOUBLE)
        assertEquals(0.1235, 0.123411.ceilTo(4), DELTA_DOUBLE)
        assertEquals(123500.0, 123411.0.ceilTo(4), DELTA_DOUBLE)
    }
}
