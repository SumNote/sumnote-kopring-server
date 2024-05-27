package com.capston.sumnote.member.controller

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.capston.sumnote.member.dto.LoginDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {

        // DB 초기화
        clearDatabase()
    }

    @Test
    @DisplayName("로그인_200")
    fun 로그인_200() {

        // given
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM MEMBERS")
        assert(queryForList.count() == 1) { "회원이 1명 존재해야 합니다." }
    }

    @Test
    @DisplayName("로그인_400")
    fun 로그인_400() {

        // given
        val loginDto = LoginDto.Req("testexample.com", "테스트 사용자")

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // then
        resultActions
            .andExpect(status().isBadRequest)
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM MEMBERS")
        assert(queryForList.isEmpty()) { "회원이 0명 존재해야 합니다." }
    }

    @Test
    @DisplayName("회원탈퇴_200")
    fun 회원탈퇴_200() {

        // given
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")

        // 로그인 요청
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // 로그인 결과에서 Authorization 헤더 추출
        val authorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization")

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/member/withdraw/{email}", "test@example.com")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM MEMBERS")
        assert(queryForList.isEmpty()) { "회원이 0명이어야 합니다." }
    }

    @Test
    @DisplayName("회원탈퇴_400")
    fun 회원탈퇴_400() {
        
        // given
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")
        
        // 로그인 요청
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )
        
        // 로그인 결과에서 Authorization 헤더 추출
        val authorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization")
        
        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/member/withdraw/{email}", "testexample.com")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )
        
        // then
        resultActions
            .andExpect(status().isBadRequest)
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM MEMBERS")
        assert(queryForList.count() == 1) { "회원이 1명이어야 합니다." }
    }

    @Test
    @DisplayName("회원탈퇴_404")
    fun 회원탈퇴_404() {

        // given
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")

        // 로그인 요청
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // 로그인 결과에서 Authorization 헤더 추출
        val authorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization")

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/member/withdraw/{email}", "nonexist@example.com")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isNotFound)
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM MEMBERS")
        assert(queryForList.count() == 1) { "회원이 1명이어야 합니다." }
    }

    private fun clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE")
        val tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'", String::class.java)
        tables.forEach { table ->
            jdbcTemplate.execute("DELETE FROM $table") // 테이블 내 데이터 삭제
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE")
    }
}
