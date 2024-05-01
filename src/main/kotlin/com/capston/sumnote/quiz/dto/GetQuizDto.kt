package com.capston.sumnote.quiz.dto

data class GetQuizDto (
        val quizId : Long?,
        val quiz : List<QuizPageInfo>?
)
data class QuizPageInfo(
        val quizPageId: Long,
        val question: String,
        val selection: MutableList<PageSelection>,
        val answer: String,
        val commentary: String,
)

data class PageSelection(
        val selection : String,
)