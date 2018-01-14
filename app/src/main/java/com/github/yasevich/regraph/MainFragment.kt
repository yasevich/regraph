package com.github.yasevich.regraph

import android.support.v4.app.Fragment
import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus
import com.github.yasevich.regraph.repository.RepositoryResponse
import com.github.yasevich.regraph.util.async
import com.github.yasevich.regraph.util.mainThread

class MainFragment: Fragment() {

    var listener: EventListener? = null

    private var inProgress: Boolean = false

    init {
        retainInstance = true
    }

    fun requestCurrencies() {
        if (inProgress) {
            listener?.onInProgress(true)
            return
        }
        onInProgress(true)

        async {
            val response = App.instance.repository.getCurrencies()
            mainThread {
                onCurrenciesResponse(response)
            }
        }
    }

    private fun onCurrenciesResponse(response: RepositoryResponse<List<String>>) {
        when (response.status) {
            AppStatus.SUCCESS -> onCurrencies(response.result!!)
            AppStatus.REFUSED -> onRefused(response.error!!)
        }
        onInProgress(false)
    }

    private fun onInProgress(inProgress: Boolean) {
        this.inProgress = inProgress
        listener?.onInProgress(inProgress)
    }

    private fun onCurrencies(currencies: List<String>) {
        listener?.onCurrencies(currencies)
    }

    private fun onRefused(error: AppError) {
        val resId = when (error) {
            AppError.INVALID_CURRENCY -> R.string.app_error_invalid_currency
            AppError.TECHNICAL_ERROR -> R.string.app_error_technical_error
        }
        listener?.onRefused(getText(resId))
    }

    interface EventListener {
        fun onInProgress(inProgress: Boolean)
        fun onCurrencies(currencies: List<String>)
        fun onRefused(error: CharSequence)
    }
}
