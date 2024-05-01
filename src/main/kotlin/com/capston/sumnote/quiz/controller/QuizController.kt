package com.capston.sumnote.quiz.controller

import com.capston.sumnote.quiz.dto.CreateQuizDto
import com.capston.sumnote.quiz.service.QuizService
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quiz")
class QuizController (private var quizService: QuizService){

    private fun getCurrentUserEmail(): String =
            (SecurityContextHolder.getContext().authentication.principal as? User)?.username
                    ?: "사용자를 찾을 수 없습니다."

    @PostMapping
    fun createQuiz(@RequestBody dto: CreateQuizDto): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.createQuiz(dto, getCurrentUserEmail())
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getQuiz(): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.getQuiz(getCurrentUserEmail())
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{quizId}")
    fun getOneQuiz(@PathVariable("quizId") quizId: Long): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.getOneQuiz(getCurrentUserEmail(), quizId)
        return ResponseEntity.status(response.status).body(response)
    }
}