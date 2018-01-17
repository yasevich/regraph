package com.github.yasevich.regraph.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.CurrencyRate
import com.github.yasevich.regraph.util.showToast
import kotlinx.android.synthetic.main.activity_live_rates.baseCurrencySelector
import kotlinx.android.synthetic.main.activity_live_rates.liveRates

private const val TAG_FRAGMENT_LIVE_RATES = "liveRatesFragment"

class LiveRatesActivity : AppCompatActivity(), LiveRatesContract.View, AdapterView.OnItemSelectedListener {

    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private val presenter: LiveRatesContract.Presenter by lazy { fragment.presenter }

    private lateinit var fragment: LiveRatesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_rates)
        setTitle(R.string.app_live_rates_title)
        prepareViews()

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
    }

    override fun onStart() {
        super.onStart()
        presenter.startUpdates()
    }

    override fun onStop() {
        super.onStop()
        presenter.stopUpdates()
    }

    override fun onNewBaseCurrency(baseCurrency: String, currencies: List<String>) {
        with(adapter) {
            clear()
            addAll(currencies)
        }
        baseCurrencySelector.setSelection(currencies.indexOf(baseCurrency))
    }

    override fun onNewRates(rates: List<CurrencyRate>) {
        liveRates.addPoints(rates.map { it.amount.toFloat() }.toFloatArray())
    }

    override fun onError(textResId: Int) {
        showToast(textResId, Toast.LENGTH_SHORT)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        setBaseCurrency(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        setBaseCurrency(position)
    }

    private fun prepareViews() {
        baseCurrencySelector.adapter = adapter
        baseCurrencySelector.onItemSelectedListener = this
    }

    private fun setBaseCurrency(position: Int) {
        presenter.setBaseCurrency(adapter.getItem(position))
    }

    companion object {

        private const val EXTRA_CURRENCIES = "currencies"

        fun intent(context: Context, currencies: List<String>): Intent {
            return Intent(context, LiveRatesActivity::class.java)
                    .putExtra(EXTRA_CURRENCIES, currencies.toTypedArray())
        }
    }
}
