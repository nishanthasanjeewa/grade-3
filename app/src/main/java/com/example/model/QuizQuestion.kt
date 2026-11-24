package com.example.model

data class QuizQuestion(
    val index: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

enum class EvaluationLevel {
    NOT_STARTED,
    IN_PROGRESS,
    EXCELLENT,
    VERY_GOOD,
    GOOD_EFFORT,
    KEEP_PRACTICING
}

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val userAnswers: Map<Int, Int> = emptyMap(),
    val showWrongOnly: Boolean = false,
    val isLoaded: Boolean = false
) {
    val totalCount: Int
        get() = questions.size

    val answeredCount: Int
        get() = userAnswers.size

    val correctCount: Int
        get() = questions.count { q -> userAnswers[q.index] == q.correctIndex }

    val wrongCount: Int
        get() = questions.count { q ->
            val selected = userAnswers[q.index]
            selected != null && selected != q.correctIndex
        }

    val percentage: Int
        get() = if (totalCount == 0) 0 else ((correctCount * 100f) / totalCount).let { kotlin.math.round(it).toInt() }

    val isCompleted: Boolean
        get() = totalCount > 0 && answeredCount == totalCount

    val visibleQuestions: List<QuizQuestion>
        get() = if (!showWrongOnly) {
            questions
        } else {
            questions.filter { q ->
                val selected = userAnswers[q.index]
                selected != null && selected != q.correctIndex
            }
        }

    val evaluationLevel: EvaluationLevel
        get() = when {
            answeredCount == 0 -> EvaluationLevel.NOT_STARTED
            answeredCount < totalCount -> EvaluationLevel.IN_PROGRESS
            percentage >= 85 -> EvaluationLevel.EXCELLENT
            percentage >= 65 -> EvaluationLevel.VERY_GOOD
            percentage >= 40 -> EvaluationLevel.GOOD_EFFORT
            else -> EvaluationLevel.KEEP_PRACTICING
        }
}
