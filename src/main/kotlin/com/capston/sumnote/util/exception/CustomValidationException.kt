package com.capston.sumnote.util.exception

class CustomValidationException(
    message: String,
    val statusCode: Int = 400
) :RuntimeException(message) {
}