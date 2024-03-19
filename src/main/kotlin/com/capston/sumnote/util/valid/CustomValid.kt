package com.capston.sumnote.util.valid

import java.util.regex.Pattern

object CustomValid {

    private const val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"

    fun isEmailRegexValid(email: String): Boolean {
        return Pattern.compile(emailRegex).matcher(email).matches()
    }

}