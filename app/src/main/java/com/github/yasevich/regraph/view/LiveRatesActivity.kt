package com.github.yasevich.regraph.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.util.showToast
import kotlinx.android.synthetic.main.activity_graph.log

private const val TAG_FRAGMENT_LIVE_RATES = "liveRatesFragment"

class LiveRatesActivity : AppCompatActivity(), LiveRatesContract.View {

    private val presenter: LiveRatesContract.Presenter by lazy { fragment.presenter }

    private lateinit var fragment: LiveRatesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        if (savedInstanceState == null) {
            fragment = LiveRatesFragment()
            supportFragmentManager.beginTransaction()
                    .add(LiveRatesFragment(), TAG_FRAGMENT_LIVE_RATES)
                    .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_LIVE_RATES) as LiveRatesFragment
        }

        presenter.view = this
        presenter.setCurrencies(intent.getStringArrayExtra(EXTRA_CURRENCIES).toList())
        presenter.startUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stopUpdates()
    }

    override fun onNewBaseCurrency(baseCurrency: String, currencies: List<String>) {
        log.text = baseCurrency
    }

    override fun onNewRates(rates: List<CurrencyRate>) {
        log.text = rates.toString()
    }

    override fun onError(textResId: Int) {
        showToast(textResId, Toast.LENGTH_SHORT)
    }

    companion object {

        private const val EXTRA_CURRENCIES = "currencies"

        fun intent(context: Context, currencies: List<String>): Intent {
            return Intent(context, LiveRatesActivity::class.java)
                    .putExtra(EXTRA_CURRENCIES, currencies.toTypedArray())
        }
    }
}
