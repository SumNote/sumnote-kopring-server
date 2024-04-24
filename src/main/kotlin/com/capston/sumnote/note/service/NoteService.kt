package com.capston.sumnote.note.service

import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse

interface NoteService {
    fun createNote(dto: CreateNoteDto, email: String): CustomApiResponse<*>
    fun findRecentNotesLimited(email: String): CustomApiResponse<*>
    fun findAllNotesSorted(email: String): CustomApiResponse<*>
    fun getNote(noteId: Long): CustomApiResponse<*>
}
