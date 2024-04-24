package com.capston.sumnote.note.dto

import java.time.LocalDateTime
import com.capston.sumnote.util.response.DateFormatter

class GetNotesDto(
    val noteId: Long,
    val title: String,
    createdAt: LocalDateTime,
    lastModifiedAt: LocalDateTime
) {
    val createdAt: String = DateFormatter.formatDateTime(createdAt)
    val lastModifiedAt: String = DateFormatter.formatDateTime(lastModifiedAt)
}
