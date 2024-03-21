package com.capston.sumnote.util.security

import com.capston.sumnote.util.response.CustomApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    // 401 인 경우 CustomApiResponse 를 사용하여 응답 내려주기
    override fun commence(request: HttpServletRequest?, response: HttpServletResponse, authException: AuthenticationException?) {
        response.contentType = "application.json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val out = response.outputStream
        val mapper = ObjectMapper()
        mapper.writeValue(out, CustomApiResponse.createFailWithoutData(HttpStatus.UNAUTHORIZED.value(), "토큰을 확인해 주세요."))
        out.flush()
    }
}