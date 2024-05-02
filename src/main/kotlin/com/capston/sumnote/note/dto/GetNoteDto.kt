package com.capston.sumnote.note.dto

data class GetNoteDto(
    val note: NoteDetail,
    val notePages: List<NotePageDetail>
)

data class NoteDetail(
    val noteId: Long,
    val title: String
)

data class NotePageDetail(
    val notePageId: Long,
    val title: String,
    val content: String
)
