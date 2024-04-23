package com.capston.sumnote.note.repository

import com.capston.sumnote.domain.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.capston.sumnote.note.dto.GetNotesDto


@Repository
interface NoteRepository : JpaRepository<Note, Long> {
    fun findTop5ByMemberEmailOrderByLastModifiedAtDesc(email: String): List<GetNotesDto>
    fun findAllByMemberEmailOrderByLastModifiedAtDesc(email: String): List<GetNotesDto>
}
