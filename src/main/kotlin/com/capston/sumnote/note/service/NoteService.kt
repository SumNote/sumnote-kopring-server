package com.capston.sumnote.note.service

import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse

interface NoteService {
    fun createNote(dto: CreateNoteDto, email: String): CustomApiResponse<*>
    fun findNotesByType(email: String, type: String): CustomApiResponse<*>
    fun getNote(email: String, noteId: Long): CustomApiResponse<*>
}
