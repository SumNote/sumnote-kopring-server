package com.capston.sumnote.note.dto

import com.capston.sumnote.util.response.DateFormatter
import java.time.LocalDateTime

class GetNotesDto(
    val noteId: Long,
    val title: String,
    createdAt: LocalDateTime,
    lastModifiedAt: LocalDateTime
) {
    val createdAt: String = DateFormatter.formatDateTime(createdAt)
    val lastModifiedAt: String = DateFormatter.formatDateTime(lastModifiedAt)
}
