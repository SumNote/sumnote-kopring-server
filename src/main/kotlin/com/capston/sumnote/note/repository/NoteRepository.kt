package com.capston.sumnote.note.repository

import com.capston.sumnote.domain.Note
import com.capston.sumnote.note.dto.GetNotesDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : JpaRepository<Note, Long> {
    @Query("SELECT new com.capston.sumnote.note.dto.GetNotesDto(n.id, n.title, n.createdAt, n.lastModifiedAt) FROM Note n WHERE n.member.email = :email ORDER BY n.lastModifiedAt DESC LIMIT 5")
    fun findNoteAtHome(email: String): List<GetNotesDto>

    @Query("SELECT new com.capston.sumnote.note.dto.GetNotesDto(n.id, n.title, n.createdAt, n.lastModifiedAt) FROM Note n WHERE n.member.email = :email ORDER BY n.lastModifiedAt DESC")
    fun findNoteAtAll(email: String): List<GetNotesDto>

}
