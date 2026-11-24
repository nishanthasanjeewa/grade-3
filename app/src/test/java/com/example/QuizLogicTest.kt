package com.example

import com.example.data.BuddhismQuestions
import com.example.model.EvaluationLevel
import com.example.model.QuizUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizLogicTest {
    @Test
    fun `questions dataset has 50 valid questions`() {
        val questions = BuddhismQuestions.all
        assertEquals(50, questions.size)
        questions.forEachIndexed { idx, q ->
            assertEquals(idx, q.index)
            assertEquals(4, q.options.size)
            assertTrue(q.correctIndex in 0..3)
            assertTrue(q.question.isNotBlank())
        }
    }

    @Test
    fun `ui state computes score, wrong filter, and evaluation levels accurately`() {
        val questions = BuddhismQuestions.all
        val emptyState = QuizUiState(questions = questions)
        assertEquals(EvaluationLevel.NOT_STARTED, emptyState.evaluationLevel)
        assertEquals(0, emptyState.percentage)

        // Answer all 50: 45 correct, 5 wrong -> 90% (EXCELLENT)
        val answers = buildMap {
            questions.forEach { q ->
                val chosen = if (q.index < 5) (q.correctIndex + 1) % 4 else q.correctIndex
                put(q.index, chosen)
            }
        }
        val completedState = QuizUiState(
            questions = questions,
            userAnswers = answers,
            showWrongOnly = true
        )
        assertEquals(50, completedState.answeredCount)
        assertEquals(45, completedState.correctCount)
        assertEquals(5, completedState.wrongCount)
        assertEquals(90, completedState.percentage)
        assertEquals(EvaluationLevel.EXCELLENT, completedState.evaluationLevel)
        assertEquals(5, completedState.visibleQuestions.size)
    }
}
