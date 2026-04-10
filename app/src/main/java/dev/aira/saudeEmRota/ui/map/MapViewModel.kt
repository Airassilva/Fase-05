package dev.aira.saudeEmRota.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    private val _adicionarPin = MutableLiveData<Boolean>()
    val adicionarPin: LiveData<Boolean> = _adicionarPin

    fun solicitarPin() {
        _adicionarPin.value = true
    }

    fun pinAdicionado() {
        _adicionarPin.value = false
    }
}