package com.zelyder.mediaclient.domain.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

suspend fun <T> Call<T>.convertToSuspend(): T = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback<T>{
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful && response.body() != null) {
                cont.resumeWith(Result.success(response.body()!!))
            }else {
                cont.resumeWith(Result.failure(Exception("response is not Successful")))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            cont.resumeWith(Result.failure(Exception("Callback failed")))
        }

    })
}