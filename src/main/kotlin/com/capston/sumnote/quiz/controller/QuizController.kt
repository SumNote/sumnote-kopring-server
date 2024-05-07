package com.capston.sumnote.quiz.controller

import com.capston.sumnote.quiz.dto.CreateQuizDto
import com.capston.sumnote.quiz.service.QuizService
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.security.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quiz")
class QuizController (private var quizService: QuizService){

    @PostMapping
    fun createQuiz(@RequestBody dto: CreateQuizDto): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.createQuiz(SecurityUtils.getCurrentUserEmail(), dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getQuiz(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.getQuiz(SecurityUtils.getCurrentUserEmail(), type)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{quizId}")
    fun getOneQuiz(@PathVariable("quizId") quizId: Long): ResponseEntity<CustomApiResponse<*>> {
        val response = quizService.getOneQuiz(SecurityUtils.getCurrentUserEmail(), quizId)
        return ResponseEntity.status(response.status).body(response)
    }
}