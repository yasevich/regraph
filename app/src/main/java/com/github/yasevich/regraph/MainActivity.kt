package com.github.yasevich.regraph

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView

private const val TAG_MAIN_FRAGMENT = "mainFragment"

class MainActivity : AppCompatActivity(), MainFragment.EventListener {

    private val list: RecyclerView by lazy { findViewById<RecyclerView>(R.id.list) }
    private val swipe: SwipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipe) }

    private lateinit var fragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fragment = MainFragment()
            supportFragmentManager
                    .beginTransaction()
                    .add(fragment, TAG_MAIN_FRAGMENT)
                    .commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG_MAIN_FRAGMENT) as MainFragment
        }

        fragment.listener = this
        fragment.requestCurrencies()
    }

    override fun onDestroy() {
        fragment.listener = null
        super.onDestroy()
    }

    override fun onInProgress(inProgress: Boolean) {
        swipe.isRefreshing = inProgress
    }

    override fun onCurrencies(currencies: List<String>) {
        list.adapter = Adapter(this).also { it.items = currencies }
    }

    override fun onRefused(error: CharSequence) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
