package com.github.yasevich.regraph.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.yasevich.regraph.R
import kotlinx.android.synthetic.main.activity_graph.log
import java.util.Arrays

class GraphActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        log.text = Arrays.toString(intent.getStringArrayExtra(EXTRA_CURRENCIES))
    }

    companion object {

        private const val EXTRA_CURRENCIES = "currencies"

        fun intent(context: Context, currencies: List<String>): Intent {
            return Intent(context, GraphActivity::class.java)
                    .putExtra(EXTRA_CURRENCIES, currencies.toTypedArray())
        }
    }
}
