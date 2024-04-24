package com.capston.sumnote.note.repository

import com.capston.sumnote.domain.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.capston.sumnote.note.dto.GetNotesDto
import org.springframework.data.jpa.repository.Query

@Repository
interface NoteRepository : JpaRepository<Note, Long> {
    @Query("SELECT new com.capston.sumnote.note.dto.GetNotesDto(n.id, n.title, n.createdAt, n.lastModifiedAt) FROM Note n WHERE n.member.email = :email ORDER BY n.lastModifiedAt DESC")
    fun findTop5ByMemberEmailOrderByLastModifiedAtDesc(email: String): List<GetNotesDto>

    @Query("SELECT new com.capston.sumnote.note.dto.GetNotesDto(n.id, n.title, n.createdAt, n.lastModifiedAt) FROM Note n WHERE n.member.email = :email ORDER BY n.lastModifiedAt DESC")
    fun findAllByMemberEmailOrderByLastModifiedAtDesc(email: String): List<GetNotesDto>

}
