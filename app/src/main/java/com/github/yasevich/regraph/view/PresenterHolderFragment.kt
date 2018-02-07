package com.github.yasevich.regraph.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.yasevich.regraph.base.BasePresenter
import com.github.yasevich.regraph.base.BaseView
import com.github.yasevich.regraph.base.BaseViewState

abstract class PresenterHolderFragment<T1 : BaseView, T2: BaseViewState, out T3 : BasePresenter<T1, T2>>: Fragment() {

    abstract val presenter: T3

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            presenter.restoreState(wrap(savedInstanceState))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveState(wrap(outState))
    }

    abstract fun wrap(bundle: Bundle): T2
}
