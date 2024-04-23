package com.capston.sumnote.note.dto


data class CreateNoteDto(
    val note: NoteInfo,
    val notePages: List<NotePageInfo>
) {
    data class NoteInfo(
        val title: String
    )

    data class NotePageInfo(
        val title: String,
        val content: String
    )
}
