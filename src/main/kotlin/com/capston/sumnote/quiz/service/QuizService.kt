package com.capston.sumnote.quiz.service

import com.capston.sumnote.quiz.dto.CreateQuizDto
import com.capston.sumnote.util.response.CustomApiResponse

interface QuizService {
    fun createQuiz(dto: CreateQuizDto, email: String): CustomApiResponse<*>

    fun getQuiz(email: String): CustomApiResponse<*>

    fun getOneQuiz(email: String, noteId: Long): CustomApiResponse<*>
}