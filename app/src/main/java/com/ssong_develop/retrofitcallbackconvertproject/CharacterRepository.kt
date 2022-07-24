package com.ssong_develop.retrofitcallbackconvertproject

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// callback을 피하기 위해서 coroutine을 잘 사용하다가도 callback으로 결과를 넘겨주는 api를 호출하면 호출 함수 내부에서 await()이나 유사한 동작을 한느 coroutine api를 사용해
// callback의 종료 시점을 기다리도록 하는 불편한 코드를 작성해야한다.

// callback을 coroutine에 보다 적합하게 사용할 수 있도록 suspendCoroutine을 제공한다.

// suspendCoroutine : 일회성 callback , return 값이 단일
// callbackFlow : 지속적으로 메시지가 전달되어 오는 callback, return 값이 flow

// return값을 단일 값으로 받느냐, stream형태로 받느냐의 차이기 때문에 어떤 걸 사용하더라도 무방하다.
// 그러나 callback의 결과가 한번 오고 끝날지, 여러 개의 결과가 반환되는지에 따라서 구분해 api를 선택하면 좀 더 간결하게 코드를 작성할 수 있다.
@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepository {
    val service = FakeRetrofit.provideRetrofit().create(apiService::class.java)

    // Repository가 Coroutine Scope를 알고 있는 형태로 작성한다면 보다 재밌게 작성할 수 있게되겠죠?

    // 그러나 repository 내에서 job을 관리한다는게 조금 안좋다고는 보이긴 한다.
    suspend fun fetchCharacter(page: Int): Wrapper<Info, Characters>? {
        val result = suspendCancellableCoroutine<Wrapper<Info, Characters>?> { continuation ->
            val callbackImpl = object : Callback<Wrapper<Info, Characters>> {
                override fun onResponse(
                    call: Call<Wrapper<Info, Characters>>,
                    response: Response<Wrapper<Info, Characters>>
                ) {
                    val res = response.body()!!.apply {
                        isNetworkSuccessTag = "success"
                    }
                    continuation.resume(res) {}
                }

                override fun onFailure(call: Call<Wrapper<Info, Characters>>, t: Throwable) {
                    // Wrapper 클래스에 성공과 실패를 나눠줄 수 있는 Value를 둬서 그걸로 Trigger해도 괜찮다,
                    // 대신 더미로 만든다던지 Response의 값이 전부 nullable하게 된다는 단점이 존재한다.
                }
            }
            // Coroutine scope이 cancel 될 때 호출된다.
            continuation.invokeOnCancellation {
                // Thread - safe한 함수만 호출되어야 한다.

            }

            service.fetchCharacter(page).enqueue(callbackImpl)
        }

        return result
    }

    // 넘겨 받는 block이 callbackFlowBuilder()의 param으로 들어갑니다.
    // 여기서 param의 정의만으로 생성된 block은 ProducerScope이라는 걸 알 수 있다.
    // 즉 callbackFlow 내부적으로 channel을 생성해 코루틴의 업무를 파이프라이닝으로 받고 해결하는 방식인 것입니다.

    // callback Flow는 cold Stream 이기 때문에 두 곳에서 collect를 한다면 동일한 결과를 두 번 반환합니다.

    suspend fun fetchCharacterWithFlow(page: Int): Flow<Wrapper<Info, Characters>> =
        callbackFlow<Wrapper<Info, Characters>> {
            val callbackImpl = object : Callback<Wrapper<Info, Characters>> {
                override fun onResponse(
                    call: Call<Wrapper<Info, Characters>>,
                    response: Response<Wrapper<Info, Characters>>
                ) {
                    trySend(response.body()!!.apply { isNetworkSuccessTag = "success" })
                }

                override fun onFailure(call: Call<Wrapper<Info, Characters>>, t: Throwable) {
                    close()
                }
            }

            service.fetchCharacter(page).enqueue(callbackImpl)

            // coroutineScope 이 cancel 또는 close 될때 호출

            // ProducerScope block의 코드의 실행이 완료되고 나서 바로 종료되는 것을 막는 코드.
            // addListener로 특정 이벤트를 관찰하는 겨웅에는 callback이 호출되는 걸 지속적으로 관찰해야 하기 때문에 api를 사용해 지속적으로 callback을 전달 받을 수 있도록 flow를 유지한다.

            // awaitClose는 flow가 cancel되거나 close 될 떄(channel close()가 명시적으로 호출될 때) 해당 블록을 호출
            // 해당 block안에서는 resource를 해제하는 코드가 들어가야한다.
            awaitClose {
                cancel()
            }
        }
}
