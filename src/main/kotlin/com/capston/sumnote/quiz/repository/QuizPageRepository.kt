package com.capston.sumnote.quiz.repository

import com.capston.sumnote.domain.QuizPage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizPageRepository : JpaRepository<QuizPage, Long> {
}