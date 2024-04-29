package com.capston.sumnote.quiz.repository

import com.capston.sumnote.domain.Quiz
import com.capston.sumnote.note.dto.GetQuizDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<Quiz, Long> {
    @Query("SELECT new com.capston.sumnote.note.dto.GetQuizDto(q.id, q.title, q.createdAt, q.lastModifiedAt) FROM Quiz q WHERE q.member.email = :email ORDER BY q.lastModifiedAt DESC")
    fun findByMemberEmailOrderByLastModifiedAtDesc(email: String): List<GetQuizDto>
}