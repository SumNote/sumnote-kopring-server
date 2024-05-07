package com.capston.sumnote.util.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

object SecurityUtils {
    fun getCurrentUserEmail(): String =
        (SecurityContextHolder.getContext().authentication.principal as? User)?.username
            ?: "사용자를 찾을 수 없습니다."
}