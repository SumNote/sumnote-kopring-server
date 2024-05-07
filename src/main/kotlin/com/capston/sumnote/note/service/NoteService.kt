package com.capston.sumnote.note.service

import com.capston.sumnote.note.dto.AddNotePageDto
import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse

interface NoteService {
    fun createNote(email: String, dto: CreateNoteDto, ): CustomApiResponse<*>
    fun findNotesByType(email: String, type: String): CustomApiResponse<*>
    fun getNote(email: String, noteId: Long): CustomApiResponse<*>
    fun changeTitle(email: String, noteId: Long, dto: ChangeTitleDto): CustomApiResponse<*>
    fun addNotePage(email: String, noteId: Long, dto: AddNotePageDto): CustomApiResponse<*>
    fun deleteNoteAndQuiz(email: String, noteId: Long): CustomApiResponse<*>
}
