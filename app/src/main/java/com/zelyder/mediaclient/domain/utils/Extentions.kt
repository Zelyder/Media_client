package com.zelyder.mediaclient.domain.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback

suspend fun <T> Call<T>.convertToSuspend(): T = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback<T>, res)
}