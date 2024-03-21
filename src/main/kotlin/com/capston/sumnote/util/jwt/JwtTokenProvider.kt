package com.capston.sumnote.util.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider : Serializable {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private lateinit var key: Key
    private val validityInMilliseconds: Long = 1000 * 60 * 60 * 24 * 14 // 토큰 유효기간 2주

    @PostConstruct
    fun init() {
        val decodedKey = Base64.getDecoder().decode(secretKey)
        this.key = SecretKeySpec(decodedKey, 0, decodedKey.size, "HmacSHA256")
    }

    fun createToken(email: String, roles: List<String>) : String {
        val claims = Jwts.claims().setSubject(email) // 사용자의 이메일을 주제로 JWT 클레임 세트 생성
        claims["roles"] = roles // 사용자 역할 정보를 클레임에 추가

        // 토큰 발행 시간과 만료 시간을 설정
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
            .setClaims(claims) // 클레임
            .setIssuedAt(now) // 발행 시간
            .setExpiration(validity) // 만료 시간
            .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘 이용
            .compact() // 토큰 생성
    }
}