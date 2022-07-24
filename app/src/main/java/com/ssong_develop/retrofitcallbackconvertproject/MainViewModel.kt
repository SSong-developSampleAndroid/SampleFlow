@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ssong_develop.retrofitcallbackconvertproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    interface NetworkResult {
        fun success(resultCode: Int)
        fun fail(cause: Throwable)
    }

    private var networkJob: Job? = null

    private fun requestNetwork(resultCallback : NetworkResult) {
        networkJob = viewModelScope.launch {
            delay(500)
            resultCallback.success(200)
        }
    }

    suspend fun connectNetwork(): Int {
        val result = suspendCancellableCoroutine<Int> { continuation ->

            val callbackImpl = object : NetworkResult {
                override fun success(resultCode: Int) {
                    continuation.resume(resultCode){}
                }

                override fun fail(cause: Throwable) {
                    TODO("Not yet implemented")
                }

            }

            requestNetwork(callbackImpl)
        }
        return result
    }

}