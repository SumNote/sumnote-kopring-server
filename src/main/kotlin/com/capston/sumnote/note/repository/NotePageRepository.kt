package com.capston.sumnote.note.repository

import com.capston.sumnote.domain.NotePage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotePageRepository: JpaRepository<NotePage, Long> {
}