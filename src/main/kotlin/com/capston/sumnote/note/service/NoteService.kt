package com.capston.sumnote.member.service

import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse

interface NoteService {
    fun createNote(dto: CreateNoteDto, email: String): CustomApiResponse<*>
}
