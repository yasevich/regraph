package com.github.yasevich.regraph.util

import android.os.AsyncTask
import com.github.yasevich.regraph.App

fun async(block: () -> Unit) {
    AsyncTask.THREAD_POOL_EXECUTOR.execute(block)
}

fun mainThread(block: () -> Unit) {
    App.instance.handler.post(block)
}
