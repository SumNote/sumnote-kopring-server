package com.capston.sumnote.quiz.controller

import com.capston.sumnote.member.dto.LoginDto
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.quiz.dto.CreateQuizDto
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


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    lateinit var authorizationHeader: String
    lateinit var createdNoteId: String

    @BeforeEach
    fun setUp() {

        // DB 초기화
        clearDatabase()

        // 로그인
        val loginDto = LoginDto.Req("test@example.com", "테스트 사용자")

        // 로그인 요청
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // 로그인 결과에서 Authorization 헤더 추출
        authorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization").toString()

        // 노트 생성
        val createOneNoteDto = createOneNoteDto()

        // 노트 생성 요청
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // 생성된 noteId 값 가져오기
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        createdNoteId = data["note_id"].toString()

    }

    // 문제집 생성
    // api/quiz
    @Test
    @DisplayName("문제집_새로_생성_201")
    fun 문제집_생성_201() {

        // given
        // noteId로 문제집 생성 준비
        val createOneQuizDto = createOneQuizDto()

        // when
        // 문제집 생성 요청 보내기
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneQuizDto))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andDo(MockMvcResultHandlers.print())

        val quizDocQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_DOC")
        val quizPageQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_PAGE WHERE quiz_id = ?", quizDocQueryForList.get(0)["quiz_id"])

        assert(quizDocQueryForList.size == 1) { "전체 퀴즈 문서 개수는 1개여야 합니다." }
        assert(quizPageQueryForList.size == 1) { "해당 퀴즈 문서의 퀴즈 페이지는 1개여야 합니다." }

    }

    @Test
    @DisplayName("문제집_기존꺼에_생성_201")
    fun 문제집_기존꺼에_201() {

        // given
        // noteId로 문제집 생성 준비
        val createOneQuizDto = createOneQuizDto()

        // 문제집 1개 미리 생성
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneQuizDto))
        )

        // when
        // 문제집 생성 요청 보내기 - 이미 생성되어 있으므로 퀴즈 문서는 1개이고 퀴즈 페이지는 2개
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneQuizDto))
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andDo(MockMvcResultHandlers.print())

        val quizDocQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_DOC")
        val quizPageQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_PAGE WHERE quiz_id = ?", quizDocQueryForList.get(0)["quiz_id"])

        assert(quizDocQueryForList.size == 1) { "전체 퀴즈 문서 개수는 1개여야 합니다." }
        assert(quizPageQueryForList.size == 2) { "해당 퀴즈 문서의 퀴즈 페이지는 2개여야 합니다." }

    }

    @Test
    @DisplayName("문제집_생성_401")
    fun 문제집_생성_401() {

        // given
        // noteId로 문제집 생성 준비
        val createOneQuizDto = createOneQuizDto()

        // when
        // 문제집 생성 요청 보내기
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz")
                .header("Authorization", authorizationHeader + "x")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneQuizDto))
        )

        // then
        resultActions
            .andExpect(status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())

        val quizDocQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_DOC")
        val quizPageQueryForList = jdbcTemplate.queryForList("SELECT * FROM QUIZ_PAGE")

        assert(quizDocQueryForList.isEmpty()) { "퀴즈 문서는 생성되지 않아야 합니다." }
        assert(quizPageQueryForList.isEmpty()) { "퀴즈 페이지는 생성되지 않아야 합니다." }
    }

    // 모든 문제집 조회
    // api/quiz?type=all
    // api/quiz?type=home
    @Test
    @DisplayName("모든_문제집_조회_home_200")
    fun 모든_문제집_조회_home_200() {

    }

    @Test
    @DisplayName("모든_문제집_조회_all_200")
    fun 모든_문제집_조회_all_200() {

    }

    @Test
    @DisplayName("모든_문제집_조회_home_401")
    fun 모든_문제집_조회_home_401() {

    }

    // 특정 문제집 모든 퀴즈 조회
    // api/quiz/{id}
    @Test
    @DisplayName("특정_문제집_모든_퀴즈_200")
    fun 특정_문제집_모든_퀴즈_200() {

    }

    @Test
    @DisplayName("특정_문제집_모든_퀴즈_401")
    fun 특정_문제집_모든_퀴즈_401() {

    }

    private fun createOneNoteDto(): CreateNoteDto {

        val noteInfo = CreateNoteDto.NoteInfo("테스트 노트입니다.")
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

        return CreateNoteDto(noteInfo, notePages)
    }

    private fun createOneQuizDto(): CreateQuizDto {

        val quizPages = listOf(
            CreateQuizDto.QuizInfo(
                question = "태양에 가장 가까운 행성은 무엇인가요?",
                selection = listOf("1.수성", "2.금성", "3.지구", "4.화성"),
                answer = "1",
                commentary = "태양에 가장 가까운 행성은 '수성'입니다. 따라서 정답은 1번입니다."
            )
        )

        return CreateQuizDto(createdNoteId.toLong(), "생성된 퀴즈 노트입니다.", quizPages)
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