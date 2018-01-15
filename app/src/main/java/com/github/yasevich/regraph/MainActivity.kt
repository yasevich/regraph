package com.github.yasevich.regraph

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast

private const val TAG_MAIN_FRAGMENT = "mainFragment"

class MainActivity : AppCompatActivity(), MainScreenContract.View, SwipeRefreshLayout.OnRefreshListener {

    private val adapter = Adapter(this)

    private val swipe: SwipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipe) }

    private lateinit var presenter: MainScreenContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.list).adapter = adapter

        swipe.setOnRefreshListener(this)

        if (savedInstanceState == null) {
            presenter = MainFragment().also {
                supportFragmentManager
                        .beginTransaction()
                        .add(it, TAG_MAIN_FRAGMENT)
                        .commit()
            }
        } else {
            presenter = supportFragmentManager.findFragmentByTag(TAG_MAIN_FRAGMENT) as MainFragment
        }

        presenter.view = this
        presenter.requestCurrencies()
    }

    override fun onDestroy() {
        presenter.view = null
        super.onDestroy()
    }

    override fun onInProgress(inProgress: Boolean) {
        swipe.isRefreshing = inProgress
    }

    override fun onCurrencies(currencies: List<String>) {
        with(adapter) {
            items = currencies
            notifyDataSetChanged()
        }
    }

    override fun onRefused(error: CharSequence) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
    override fun onRefresh() {
        presenter.requestCurrencies()
    }

    private class Adapter(private val context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        var items: List<String> = emptyList()

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
            holder.textView.text = items[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder =
                ViewHolder(context, parent)

        private class ViewHolder(
                context: Context,
                parent: ViewGroup
        ) : RecyclerView.ViewHolder(
                LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_checked, parent, false)
        ) {
            val textView: CheckedTextView = itemView as CheckedTextView
        }
    }
}
