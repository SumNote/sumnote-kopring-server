package com.capston.sumnote.util.response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy.M.d. a h:mm")

    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }
}