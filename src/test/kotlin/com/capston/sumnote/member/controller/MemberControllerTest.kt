package com.capston.sumnote.member.controller

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class MemberControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        clearDatabase()

        /**
         * 멤버 생성
         */
    }

    @Test
    @DisplayName("로그인")
    fun 로그인() {

        /**
         * 생성한 멤버로 로그인
         * 경로: api/member/login
         * 토큰값 검증
         */
    }


    @Test
    @DisplayName("회원 탈퇴")
    fun 회원_탈퇴() {

        /**
         * 생성한 멤버 탈퇴
         * 경로: api/member/withdraw/{email}
         * 멤버 존재하지 않음을 확인
         */
    }

    private fun clearDatabase() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0") // FK 제약 조건 비활성화
        val tables = jdbcTemplate.queryForList("SHOW TABLES", String::class.java)
        tables.forEach { table ->
            jdbcTemplate.execute("TRUNCATE TABLE $table") // 테이블 내 데이터 삭제
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1") // FK 제약 조건 활성화
    }

}