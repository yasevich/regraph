package com.github.yasevich.regraph.util

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

operator fun <T> KMutableProperty0<T>.setValue(any: Any, property: KProperty<*>, value: T) = set(value)

operator fun <T> KProperty0<T>.getValue(any: Any, property: KProperty<*>): T = get()
