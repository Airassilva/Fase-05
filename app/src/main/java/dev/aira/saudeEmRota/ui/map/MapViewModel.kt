package dev.aira.saudeEmRota.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    private val _adicionarPin = MutableLiveData<Boolean>()

    private val _busca = MutableLiveData<String>()

    val busca: LiveData<String> = _busca
    val adicionarPin: LiveData<Boolean> = _adicionarPin

    fun buscarUsf(query: String) {
        _busca.value = query
    }

    fun solicitarPin() {
        _adicionarPin.value = true
    }

    fun pinAdicionado() {
        _adicionarPin.value = false
    }
}