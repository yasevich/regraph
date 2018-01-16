package com.github.yasevich.regraph.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(resId: Int, duration: Int) {
    Toast.makeText(this, resId, duration).show()
}
