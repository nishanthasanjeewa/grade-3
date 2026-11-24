package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BuddhismQuestions
import com.example.data.QuizProgressStore
import com.example.model.QuizUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val progressStore = QuizProgressStore(application.applicationContext)

    private val _uiState = MutableStateFlow(
        QuizUiState(questions = BuddhismQuestions.all)
    )
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = progressStore.savedAnswers.first()
            _uiState.update { state ->
                state.copy(
                    userAnswers = saved,
                    isLoaded = true
                )
            }
        }
    }

    fun selectAnswer(questionIndex: Int, selectedOptionIndex: Int) {
        var updatedAnswers: Map<Int, Int>? = null
        _uiState.update { current ->
            // Lock Answer: once answered, a question cannot be modified.
            if (current.userAnswers.containsKey(questionIndex)) {
                return@update current
            }
            val nextAnswers = current.userAnswers.toMutableMap().apply {
                put(questionIndex, selectedOptionIndex)
            }
            updatedAnswers = nextAnswers
            current.copy(userAnswers = nextAnswers)
        }
        updatedAnswers?.let { toSave ->
            viewModelScope.launch {
                progressStore.saveAnswers(toSave)
            }
        }
    }

    fun toggleWrongOnlyFilter() {
        _uiState.update { current ->
            current.copy(showWrongOnly = !current.showWrongOnly)
        }
    }

    fun showAllQuestions() {
        _uiState.update { current ->
            current.copy(showWrongOnly = false)
        }
    }

    fun resetQuiz() {
        _uiState.update { current ->
            current.copy(
                userAnswers = emptyMap(),
                showWrongOnly = false
            )
        }
        viewModelScope.launch {
            progressStore.clearAnswers()
        }
    }
}
