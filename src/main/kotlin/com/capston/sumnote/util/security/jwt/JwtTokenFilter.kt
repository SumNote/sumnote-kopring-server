package com.capston.sumnote.util.security.jwt

import com.capston.sumnote.util.response.CustomApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

class JwtTokenFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse
        val token = jwtTokenProvider.resolveToken(request)
        val requestURI = request.requestURI

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val auth = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            } else if (requestURI == "/api/member/login" || requestURI == "/api/test") { // 토큰 없는 요청 (로그인, 테스트)
                // 그대로 요청 진행
            } else {
                unauthorizedResponse(response)
                return
            }
        } catch (e: Exception) {
            unauthorizedResponse(response)
            return
        }
        filterChain.doFilter(req, res)
    }

    // 토큰 형식이 안맞아도 json 데이터 반환
    private fun unauthorizedResponse(response: HttpServletResponse) {
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val apiResponse = CustomApiResponse.createFailWithoutData(HttpServletResponse.SC_UNAUTHORIZED, "토큰을 확인해 주세요.")
        val json = ObjectMapper().writeValueAsString(apiResponse)
        response.writer.print(json)
        response.writer.close()
    }
}