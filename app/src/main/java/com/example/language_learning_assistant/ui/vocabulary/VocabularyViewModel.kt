package com.example.language_learning_assistant.ui.vocabulary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VocabularyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is vocabulary Fragment"
    }
    val text: LiveData<String> = _text
}