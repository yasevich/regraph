package com.github.yasevich.regraph.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import com.github.yasevich.regraph.CurrencySelectionContract
import com.github.yasevich.regraph.R
import com.github.yasevich.regraph.util.showToast
import kotlinx.android.synthetic.main.activity_main.list
import kotlinx.android.synthetic.main.activity_main.showRates
import kotlinx.android.synthetic.main.activity_main.swipe

private const val TAG_FRAGMENT_MAIN = "mainFragment"

class MainActivity : AppCompatActivity(), CurrencySelectionContract.View {

    private val adapter = Adapter()

    private val presenter: CurrencySelectionContract.Presenter by lazy { fragment.presenter }

    private lateinit var fragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_main_title)
        prepareViews()

        if (savedInstanceState == null) {
            fragment = MainFragment()
            supportFragmentManager.beginTransaction()
                    .add(fragment, TAG_FRAGMENT_MAIN)
                    .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_MAIN) as MainFragment
        }

        with(presenter) {
            view = this@MainActivity
            requestCurrencies()
        }
    }

    override fun onDestroy() {
        presenter.view = null
        super.onDestroy()
    }

    override fun onCurrencies(currencies: List<Pair<String, Boolean>>) {
        with(adapter) {
            items = currencies
            notifyDataSetChanged()
        }
    }

    override fun onCurrenciesLoading(inProgress: Boolean) {
        swipe.isRefreshing = inProgress
    }

    override fun onCurrenciesSelection(valid: Boolean) {
        showRates.isEnabled = valid
    }

    override fun onCurrencySelectionChanged(currencies: List<Pair<String, Boolean>>, position: Int) {
        with(adapter) {
            items = currencies
            notifyItemChanged(position)
        }
    }

    override fun onShowGraph(currencies: List<String>) {
        startActivity(LiveRatesActivity.intent(this, currencies))
    }

    override fun onError(textResId: Int) {
        showToast(textResId, Toast.LENGTH_SHORT)
    }

    private fun prepareViews() {
        list.adapter = adapter
        swipe.setOnRefreshListener({
            presenter.requestCurrencies()
        })
        showRates.setOnClickListener({
            presenter.showGraph()
        })
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        var items: List<Pair<String, Boolean>> = emptyList()

        private val inflater: LayoutInflater by lazy { LayoutInflater.from(this@MainActivity) }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.textView) {
                val item = items[position]
                text = item.first
                isChecked = item.second

                setOnClickListener {
                    if (isChecked) {
                        presenter.removeSelectedCurrency(item.first)
                    } else {
                        presenter.addSelectedCurrency(item.first)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(inflater, parent)
    }

    private class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(android.R.layout.simple_list_item_checked, parent, false)) {
        val textView: CheckedTextView = itemView as CheckedTextView
    }
}
