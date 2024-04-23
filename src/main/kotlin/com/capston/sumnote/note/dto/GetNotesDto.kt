package com.capston.sumnote.note.dto

import java.time.LocalDateTime

class GetNotesDto(
    val id:Long,
    val title: String,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime
)