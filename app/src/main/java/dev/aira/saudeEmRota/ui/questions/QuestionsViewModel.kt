package dev.aira.saudeEmRota.ui.questions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestionsViewModel : ViewModel() {

    private val _texts = MutableLiveData<List<String>>()
    val texts: LiveData<List<String>> = _texts

    init {
        // carregue os dados aqui futuramente
        _texts.value = emptyList()
    }
}