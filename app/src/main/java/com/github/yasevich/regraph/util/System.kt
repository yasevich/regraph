package com.github.yasevich.regraph.util

import java.util.TimeZone

const val SECONDS_IN_DAY = 24 * 60 * 60

fun currentTimeSeconds(timeZone: TimeZone = TimeZone.getDefault()): Long =
        (System.currentTimeMillis() + timeZone.rawOffset) / 1000

fun secondsAtStartOfDay(seconds: Long): Long = seconds / SECONDS_IN_DAY * SECONDS_IN_DAY
