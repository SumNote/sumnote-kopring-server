package com.capston.sumnote.quiz.controller

import com.capston.sumnote.quiz.dto.CreateQuizDto
import com.capston.sumnote.quiz.service.QuizService
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.security.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping
    fun createQuiz(@RequestBody dto: CreateQuizDto): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = quizService.createQuiz(userEmail, dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getQuiz(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = quizService.getQuiz(userEmail, type)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{quizId}")
    fun getOneQuiz(@PathVariable("quizId") quizId: Long): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = quizService.getOneQuiz(userEmail, quizId)
        return ResponseEntity.status(response.status).body(response)
    }
}
