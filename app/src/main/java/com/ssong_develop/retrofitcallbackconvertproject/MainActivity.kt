package com.ssong_develop.retrofitcallbackconvertproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val repository = CharacterRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pageNum = MutableStateFlow(1)

        val testView = findViewById<TextView>(R.id.test)

        testView.setOnClickListener {
            pageNum.value += 1
        }

        GlobalScope.launch {
            launch {
                /*val res = repository.fetchCharacter(1)
                Log.d("ssong-develop",res.toString())*/
            }

            launch {
                pageNum.collectLatest { pageNum ->
                    repository.fetchCharacterWithFlow(pageNum).collectLatest {
                        Log.d("ssong-develop1",it.toString())
                    }
                }
            }
        }
    }
}