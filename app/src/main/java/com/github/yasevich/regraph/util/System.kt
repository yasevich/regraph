package com.github.yasevich.regraph.util

import java.util.TimeZone

fun currentTimeSeconds(timeZone: TimeZone = TimeZone.getDefault()): Long {
    return (System.currentTimeMillis() + timeZone.rawOffset) / 1000
}
