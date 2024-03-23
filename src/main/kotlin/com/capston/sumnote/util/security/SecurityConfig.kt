package com.capston.sumnote.util.security

import org.springframework.security.config.annotation.web.invoke
import com.capston.sumnote.util.security.jwt.JwtTokenFilter
import com.capston.sumnote.util.security.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
    ) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize("/api/member/login", permitAll) // 로그인 경로는 누구나 접근 가능
                authorize("/api/member/re-login", permitAll) // 재로그인 경로도 누구나 접근 가능
                authorize(anyRequest, authenticated) // 그 외 모든 요청은 인증 필요
            }
            httpBasic {} // HTTP 기본 인증 활성화 (필요한 경우)
            formLogin { disable() } // 폼 로그인 비활성화 (JWT 사용 시 일반적으로 비활성화)
            // JwtTokenFilter 추가
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtTokenFilter(jwtTokenProvider))
            exceptionHandling {
                authenticationEntryPoint = customAuthenticationEntryPoint // 응답 401 내려주기 등록
            }
        }
        return http.build()
    }

    @Bean
    fun jwtTokenFilter(): JwtTokenFilter {
        return JwtTokenFilter(jwtTokenProvider)
    }

    // PasswordEncoder 빈이 필요한 경우 추가
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
