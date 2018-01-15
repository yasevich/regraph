package com.github.yasevich.regraph

import android.support.v4.app.Fragment

class MainFragment : Fragment() {

    val presenter: MainScreenContract.Presenter = App.instance.createMainScreenPresenter()

    init {
        retainInstance = true
    }
}
