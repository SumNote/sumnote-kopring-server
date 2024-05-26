package com.capston.sumnote.member.controller

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.capston.sumnote.domain.Member
import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.member.service.MemberServiceImpl
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

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun setUp() {
        clearDatabase()

        // 데이터베이스에 회원 추가
        val member = Member(
            email = "test@example.com",
            name = "테스트 사용자"
        )

        memberRepository.save(member)

        // 필요한 초기화 작업을 여기에 추가
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
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.name").value("테스트 사용자"))
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(header().exists("Authorization"))
            .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val memberCount = memberRepository.count()
        assert(memberCount == 1L) { "회원이 1명 존재해야 합니다." }
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
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
            .andExpect(jsonPath("$.message").value("회원탈퇴 성공"))
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val memberCount = memberRepository.count()
        assert(memberCount == 0L) { "회원이 0명이어야 합니다." }
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
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
            .andExpect(jsonPath("$.message").value("이메일 형식을 맞춰주세요."))
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val memberCount = memberRepository.count()
        assert(memberCount == 1L) { "회원이 1명이어야 합니다." }
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
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
            .andExpect(jsonPath("$.message").value("존재하지 않는 이메일입니다."))
            .andDo(MockMvcResultHandlers.print()) // 요청 및 응답 로깅

        // 데이터베이스 상태 검증
        val memberCount = memberRepository.count()
        assert(memberCount == 1L) { "회원이 1명이어야 합니다." }
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
