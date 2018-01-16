package com.github.yasevich.regraph.view

import android.support.v4.app.Fragment

abstract class PresenterHolderFragment<out T>: Fragment() {

    abstract val presenter: T

    init {
        retainInstance = true
    }
}