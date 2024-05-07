package com.capston.sumnote.util.security

import com.capston.sumnote.util.exception.EntityNotFoundException
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

object SecurityUtils {
    fun getCurrentUserEmail(): String {
        val user = SecurityContextHolder.getContext().authentication.principal as? User
        return user?.username ?: throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
    }
}