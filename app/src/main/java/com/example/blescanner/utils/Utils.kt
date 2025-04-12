package com.example.blescanner.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import timber.log.Timber

inline fun <reified T> T.logd(message: String) {
    Timber.tag(T::class.java.simpleName).d(message)
}

inline fun <reified T> T.logi(message: String) {
    Timber.tag(T::class.java.simpleName).i(message)
}

inline fun <reified T> T.logw(message: String) {
    Timber.tag(T::class.java.simpleName).w(message)
}

inline fun <reified T> T.loge(message: String, throwable: Throwable? = null) {
    Timber.tag(T::class.java.simpleName).e(throwable, message)
}

@Composable
fun logCompositions(tag: String) {
    val count = remember { mutableIntStateOf(0) }
    SideEffect {
        count.intValue++
        Timber.tag(tag).d("$tag: Recomposition #${count.intValue}")
    }
}