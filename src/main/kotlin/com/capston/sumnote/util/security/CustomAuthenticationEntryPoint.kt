package com.capston.sumnote.util.security

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import com.capston.sumnote.util.response.CustomApiResponse
import com.fasterxml.jackson.databind.ObjectMapper

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val apiResponse = CustomApiResponse.createFailWithoutData(
            HttpServletResponse.SC_UNAUTHORIZED,
            "토큰을 확인해 주세요."
        )
        val json = ObjectMapper().writeValueAsString(apiResponse)
        response.writer.use { writer ->
            writer.print(json) // JSON 문자열을 응답에 출력
        }
    }
}
