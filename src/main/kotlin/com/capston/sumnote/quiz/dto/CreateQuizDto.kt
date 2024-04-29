package com.capston.sumnote.quiz.dto

data class CreateQuizDto (
        val title : String,
        val quiz : List<QuizInfo>
) {
    data class QuizInfo(
            val question: String,
            val selection: List<String>,
            val answer: String,
            val commentary: String,
    )
}