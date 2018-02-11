package com.github.yasevich.regraph.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import com.github.yasevich.regraph.LiveRatesContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.model.CurrencyRatesHistory
import com.github.yasevich.regraph.util.PaletteColorPicker
import com.github.yasevich.regraph.util.showToast
import kotlinx.android.synthetic.main.activity_live_rates.legend
import kotlinx.android.synthetic.main.activity_live_rates.liveRates

private const val TAG_FRAGMENT_LIVE_RATES = "liveRatesFragment"

class LiveRatesActivity : AppCompatActivity(), LiveRatesContract.View {

    private val presenter: LiveRatesContract.Presenter by lazy { fragment.presenter }

    private val colorMap: CurrencyColorMap = CurrencyColorMapImpl(PaletteColorPicker())
    private val currencies: MutableList<String> = mutableListOf()

    private var baseIndex: Int = 0

    private lateinit var fragment: LiveRatesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_rates)
        setTitle(R.string.app_live_rates_title)

        legend.movementMethod = LinkMovementMethod.getInstance()

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

    override fun onDestroy() {
        super.onDestroy()
        presenter.stopUpdates()
    }

    override fun onBaseCurrency(baseIndex: Int) {
        this.baseIndex = baseIndex
        legend.text = currencies.foldIndexed(SpannableStringBuilder()) { index, acc, item ->
            acc.append(SpannableString(item).apply {
                setSpan(CurrencyClickableSpan(presenter, item, index == baseIndex),
                        0, item.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(ForegroundColorSpan(colorMap.getColor(item)),
                        0, item.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }).append(' ')
        }.trim()
    }

    override fun onCurrencies(currencies: List<String>, baseIndex: Int) {
        with(this.currencies) {
            clear()
            addAll(currencies)
        }
        onBaseCurrency(baseIndex)
    }

    override fun onNewRates(history: CurrencyRatesHistory) {
        liveRates.show(history.graphs(colorMap))
    }

    override fun onError(textResId: Int) {
        showToast(textResId, Toast.LENGTH_SHORT)
    }

    companion object {

        const val EXTRA_CURRENCIES = "currencies"

        fun intent(context: Context, currencies: List<String>): Intent {
            return Intent(context, LiveRatesActivity::class.java)
                    .putExtra(EXTRA_CURRENCIES, ArrayList(currencies))
        }
    }

    private class CurrencyClickableSpan(
            private val presenter: LiveRatesContract.Presenter,
            private val currency: String,
            private val base: Boolean
    ) : ClickableSpan() {

        override fun onClick(widget: View) {
            presenter.setBaseCurrency(currency)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = base
        }
    }
}
