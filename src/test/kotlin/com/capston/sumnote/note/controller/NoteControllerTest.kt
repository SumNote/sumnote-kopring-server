package com.capston.sumnote.note.controller

import com.capston.sumnote.domain.Member
import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.note.repository.NotePageRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var noteRepository: NoteRepository

    @Autowired
    lateinit var notePageRepository: NotePageRepository

    lateinit var authorizationHeader: String

    @BeforeEach
    fun setUp(){

        // DB 초기화
        clearDatabase()

        // 데이터베이스에 넣을 Member 생성
        val member = Member(email = "test@example.com", name = "테스트 사용자")

        // 데이터베이스에 저장
        memberRepository.save(member)

        // 로그인
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // 이후 모든 요청에 들어갈 authorizationHeader 값 얻기
        authorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization").toString()

    }


    @Test
    @DisplayName("노트생성_201")
    fun 노트생성_201() {

        // given
        val createNoteDto = createNoteDto()

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createNoteDto))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andDo(MockMvcResultHandlers.print())

        val noteCount = noteRepository.count()
        val notePageCount = notePageRepository.count()

        assert(noteCount == 1L) { "노트는 1개 생성되어야 합니다."}
        assert(notePageCount == 3L) { "노트 페이지는 3개 생성되어야 합니다."}

    }

    @Test
    @DisplayName("노트생성_401")
    fun 노트생성_401() {

        // given
        val createNoteDto = createNoteDto()

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", "x$authorizationHeader")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createNoteDto))
        )

        // then
        resultActions
            .andExpect(status().isUnauthorized)

        // 데이터베이스 상태 확인
        val noteCount = noteRepository.count()
        val notePageCount = notePageRepository.count()

        assert(noteCount == 0L) { "노트는 생성되지 않아야 합니다."}
        assert(notePageCount == 0L) { "노트 페이지 또한 생성되지 않아야 합니다."}
    }

    // 모든 노트 조회


    // 특정 노트 모든 페이지 조회


    // 노트 이름 변경


    // 특정 노트에 페이지 추가


    // 노트 삭제


    // 노트/노트페이지 객체 생성 함수
    private fun createNoteDto(): CreateNoteDto {
        // note
        val note = CreateNoteDto.NoteInfo("노트 제목입니다.")

        // notePages 리스트
        val notePages = listOf(
            CreateNoteDto.NotePageInfo(
                title = "페이지 1의 제목",
                content = "페이지 1의 내용입니다."
            ),
            CreateNoteDto.NotePageInfo(
                title = "페이지 2의 제목",
                content = "페이지 2의 내용입니다."
            ),
            CreateNoteDto.NotePageInfo(
                title = "페이지 3의 제목",
                content = "페이지 3의 내용입니다."
            )
        )

        // 객체 생성
        val createNoteDto = CreateNoteDto(note, notePages)
        return createNoteDto
    }

    private fun clearDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE")
        val tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'", String::class.java)
        tables.forEach { table ->
            jdbcTemplate.execute("DELETE FROM $table")
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE")
    }



}