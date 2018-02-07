package com.github.yasevich.regraph.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.PaletteColorPicker
import com.github.yasevich.regraph.util.showToast
import kotlinx.android.synthetic.main.activity_live_rates.legend
import kotlinx.android.synthetic.main.activity_live_rates.liveRates

private const val TAG_FRAGMENT_LIVE_RATES = "liveRatesFragment"

class LiveRatesActivity : AppCompatActivity(), LiveRatesContract.View, AdapterView.OnItemSelectedListener {

    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private val presenter: LiveRatesContract.Presenter by lazy { fragment.presenter }

    private val colorMap: CurrencyColorMap = CurrencyColorMapImpl(PaletteColorPicker())

    private lateinit var fragment: LiveRatesFragment
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_rates)
        setTitle(R.string.app_live_rates_title)

        if (savedInstanceState == null) {
            fragment = LiveRatesFragment()
            fragment.arguments = intent.extras
            supportFragmentManager.beginTransaction()
                    .add(fragment, TAG_FRAGMENT_LIVE_RATES)
                    .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_LIVE_RATES) as LiveRatesFragment
        }

        with(presenter) {
            view = this@LiveRatesActivity
            startUpdates()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_live_rates, menu)
        spinner = menu.findItem(R.id.baseCurrency).actionView as Spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stopUpdates()
    }

    override fun onBaseCurrency(baseIndex: Int) {
        spinner.setSelection(baseIndex)
    }

    override fun onCurrencies(currencies: List<String>) {
        with(adapter) {
            clear()
            addAll(currencies)
        }

        legend.text = currencies.fold(SpannableStringBuilder()) {
            acc, item -> acc.append(SpannableString(item).apply {
                setSpan(ForegroundColorSpan(colorMap.getColor(item)), 0, item.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } ).append(' ')
        }.trim()
    }

    override fun onNewRates(history: CurrencyRatesHistory) {
        liveRates.show(history.graphs(colorMap))
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

    private fun setBaseCurrency(position: Int) {
        presenter.setBaseCurrency(adapter.getItem(position))
    }

    companion object {

        const val EXTRA_CURRENCIES = "currencies"

        fun intent(context: Context, currencies: List<String>): Intent {
            return Intent(context, LiveRatesActivity::class.java)
                    .putExtra(EXTRA_CURRENCIES, ArrayList(currencies))
        }
    }
}
