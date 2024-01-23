package com.jp.test.multiplatformapplication.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.entity.RocketLaunch
import com.jp.test.multiplatformapplication.Greeting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _greetingList = MutableStateFlow<List<String>>(listOf())
    val greetingList: StateFlow<List<String>> get() = _greetingList


    private val _launchList = MutableStateFlow<List<RocketLaunch>>(listOf())
    val launchList : StateFlow<List<RocketLaunch>> get() = _launchList



    init {
        viewModelScope.launch {
            Greeting().greet().collect { phrase ->
                _greetingList.update { list -> list + phrase }
            }
        }
    }

}