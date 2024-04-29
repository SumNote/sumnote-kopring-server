package com.capston.sumnote.quiz.repository

import com.capston.sumnote.domain.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<Quiz, Long> {
}