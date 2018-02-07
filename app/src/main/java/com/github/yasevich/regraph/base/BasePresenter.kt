package com.github.yasevich.regraph.base

interface BasePresenter<T1: BaseView, in T2 : BaseViewState> {
    var view: T1?
    fun saveState(state: T2)
    fun restoreState(state: T2)
}
