package com.github.yasevich.regraph.util

import android.content.res.AssetManager
import java.io.FileOutputStream

fun AssetManager.copyToInternalStorage(inFileName: String, outFileName: String) {
    FileOutputStream(outFileName).use { out -> open(inFileName).use { it.copyTo(out) }}
}
