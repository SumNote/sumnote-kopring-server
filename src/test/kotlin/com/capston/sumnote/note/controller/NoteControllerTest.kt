package com.capston.sumnote.note.controller

import com.capston.sumnote.domain.Member
import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.dto.AddNotePageDto
import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.note.repository.NotePageRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
    lateinit var tmpAuthorizationHeader: String

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
        val createNoteDto = createOneNoteDto()

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
        val createNoteDto = createOneNoteDto()

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", "authorizationHeader" + "x")
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
    @Test
    @DisplayName("모든_노트_조회_홈_200")
    fun 모든_노트_조회_홈_200() {

        // given
        val createNoteDtoList = createNoteDtoWithNum(7)
        createNoteDtoList.forEach { createNoteDto ->
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sum-note")
                    .header("Authorization", authorizationHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createNoteDto))
            ).andExpect(status().isCreated)
        }

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "home")
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data", Matchers.hasSize<Any>(5)))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("모든_노트_조회_상세_200")
    fun 모든_노트_조회_상세_200() {

        // given
        val createNoteDtoList = createNoteDtoWithNum(7)
        createNoteDtoList.forEach { createNoteDto ->
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sum-note")
                    .header("Authorization", authorizationHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createNoteDto))
            ).andExpect(status().isCreated)
        }

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "all")
        )

        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data", Matchers.hasSize<Any>(7)))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    @DisplayName("모든_노트_조회_401")
    fun 모든_노트_조회_401() {

        // given
        val createNoteDtoList = createNoteDtoWithNum(7)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createNoteDtoList))
        )

        // when
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note")
                .header("Authorization", "authorizationHeader" + "x")
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "home")
        )

        // then
        resultActions
            .andExpect(status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())

    }


    // 특정 노트 모든 페이지 조회
    @Test
    @DisplayName("특정_노트_모든_페이지_조회_200")
    fun 특정_노트_모든_페이지_조회_200() {

        // given
        val createOneNoteDto = createOneNoteDto()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when

        // DB 에 존재하는 하나의 noteId 찾기
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val noteData = queryForList.get(0)
        val noteId = noteData["note_id"]

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note/{noteId}", noteId)
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.notePages", Matchers.hasSize<Any>(3)))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    @DisplayName("특정_노트_모든_페이지_조회_401")
    fun 특정_노트_모든_페이지_조회_401() {

        // given
        val createOneNoteDto = createOneNoteDto()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note/{noteId}", noteId)
                .header("Authorization", "authorizationHeader" + "x")
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    @DisplayName("특정_노트_모든_페이지_조회_403")
    fun 특정_노트_모든_페이지_조회_403() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        ).andDo(MockMvcResultHandlers.print())

        createTmpUser()

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val noteData = queryForList.get(0)
        val noteId = noteData["note_id"]

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note/{noteId}", noteId)
                .header("Authorization", tmpAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isForbidden)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    @DisplayName("특정_노트_모든_페이지_조회_404")
    fun 특정_노트_모든_페이지_조회_404() {

        // given
        val createOneNoteDto = createOneNoteDto()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val noteData = queryForList.get(0)
        val noteId = noteData["note_id"]

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/sum-note/{noteId}", noteId.toString().toInt() + 1)
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
        )

        // then
        resultActions
            .andExpect(status().isNotFound)
            .andDo(MockMvcResultHandlers.print())

    }


    // 노트 이름 변경
    @Test
    @DisplayName("노트_이름_변경_200")
    fun 노트_이름_변경_200() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        val changeTitleDto = ChangeTitleDto("노트 제목 변경 테스트입니다.")

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/title", noteId.toString())
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTitleDto))
        )

        // then
        resultActions // 응답 확인
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())

        // 노트 제목 변경 확인
        val changedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS WHERE note_id = ?", noteId)
        val changedData = changedQueryForList.get(0)
        val changedTitle = changedData["title"]

        assertEquals("노트 제목 변경 테스트입니다.", changedTitle) { "노트 제목은 바뀌어야 합니다." }
    }

    @Test
    @DisplayName("노트_이름_변경_401")
    fun 노트_이름_변경_401() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        val changeTitleDto = ChangeTitleDto("노트 제목 변경 테스트입니다.")

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/title", noteId.toString())
                .header("Authorization", "authorizationHeader" + "x")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTitleDto))
        )

        // then
        resultActions // 응답 확인
            .andExpect(status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())

        // 노트 제목 변경 확인
        val changedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS WHERE note_id = ?", noteId)
        val changedData = changedQueryForList.get(0)
        val changedTitle = changedData["title"]

        assertEquals("노트 제목입니다.", changedTitle) { "노트 제목은 바뀌지 않아야 합니다."}
    }

    @Test
    @DisplayName("노트_이름_변경_403")
    fun 노트_이름_변경_403() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        createTmpUser()

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        val changeTitleDto = ChangeTitleDto("노트 제목 변경 테스트입니다.")

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/title", noteId.toString())
                .header("Authorization", tmpAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTitleDto))
        )

        // then
        resultActions // 응답 확인
            .andExpect(status().isForbidden)
            .andDo(MockMvcResultHandlers.print())

        // 노트 제목 변경 확인
        val changedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS WHERE note_id = ?", noteId)
        val changedData = changedQueryForList.get(0)
        val changedTitle = changedData["title"]

        assertEquals("노트 제목입니다.", changedTitle) { "노트 제목은 바뀌지 않아야 합니다."}
    }

    @Test
    @DisplayName("노트_이름_변경_404")
    fun 노트_이름_변경_404() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        // when
        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        val changeTitleDto = ChangeTitleDto("노트 제목 변경 테스트입니다.")

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/title", noteId.toString().toInt() + 1)
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeTitleDto))
        )

        // then
        resultActions // 응답 확인
            .andExpect(status().isNotFound)
            .andDo(MockMvcResultHandlers.print())

        // 노트 제목 변경 확인
        val changedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS WHERE note_id = ?", noteId)
        val changedData = changedQueryForList.get(0)
        val changedTitle = changedData["title"]

        assertEquals("노트 제목입니다.", changedTitle) { "노트 제목은 바뀌지 않아야 합니다." }
    }

    // 특정 노트에 페이지 추가
    @Test
    @DisplayName("특정_노트에_페이지_추가_200")
    fun 특정_노트에_페이지_추가_200() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        // when
        val createOneNotePageDto = createOneNotePageDto()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/add", noteId)
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNotePageDto))
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val updatedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_PAGES")
        val count = updatedQueryForList.count()
        assertEquals(4, count) { "노트의 페이지는 4개여야 합니다." }

    }

    @Test
    @DisplayName("특정_노트에_페이지_추가_401")
    fun 특정_노트에_페이지_추가_401() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        // when
        val createOneNotePageDto = createOneNotePageDto()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/add", noteId)
                .header("Authorization", authorizationHeader + "x")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNotePageDto))
        )

        // then
        resultActions
            .andExpect(status().isUnauthorized)
            .andDo(MockMvcResultHandlers.print())

        val updatedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_PAGES")
        val count = updatedQueryForList.count()
        assertEquals(3, count) { "노트의 페이지는 3개여야 합니다." }

    }

    @Test
    @DisplayName("특정_노트에_페이지_추가_403")
    fun 특정_노트에_페이지_추가_403() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        createTmpUser()

        // when
        val createOneNotePageDto = createOneNotePageDto()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/add", noteId)
                .header("Authorization", tmpAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNotePageDto))
        )

        // then
        resultActions
            .andExpect(status().isForbidden)
            .andDo(MockMvcResultHandlers.print())

        val updatedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_PAGES")
        val count = updatedQueryForList.count()
        assertEquals(3, count) { "노트의 페이지는 3개여야 합니다." }

    }

    @Test
    @DisplayName("특정_노트에_페이지_추가_404")
    fun 특정_노트에_페이지_추가_404() {

        // given
        val createOneNoteDto = createOneNoteDto()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/sum-note")
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNoteDto))
        )

        val queryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_DOCS")
        val data = queryForList.get(0)
        val noteId = data["note_id"]

        // when
        val createOneNotePageDto = createOneNotePageDto()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/sum-note/{noteId}/add", noteId.toString().toInt() + 1)
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOneNotePageDto))
        )

        // then
        resultActions
            .andExpect(status().isNotFound)
            .andDo(MockMvcResultHandlers.print())

        val updatedQueryForList = jdbcTemplate.queryForList("SELECT * FROM NOTE_PAGES")
        val count = updatedQueryForList.count()
        assertEquals(3, count) { "노트의 페이지는 3개여야 합니다." }

    }


    // 노트 삭제


    // 노트/노트페이지 객체 생성 - 개수 지정
    private fun createNoteDtoWithNum(numOfNotes: Int): List<CreateNoteDto> {
        val notes = mutableListOf<CreateNoteDto>()
        for (i in 1..numOfNotes) {
            val noteInfo = CreateNoteDto.NoteInfo(
                title = "노트 제목입니다$i"
            )
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
            notes.add(CreateNoteDto(note = noteInfo, notePages = notePages))
        }
        return notes
    }

    // 노트/노트페이지 객체 생성 - 1개만
    private fun createOneNoteDto(): CreateNoteDto {
        val noteInfo = CreateNoteDto.NoteInfo(title = "노트 제목입니다.")
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

    private fun createOneNotePageDto(): AddNotePageDto {
        return AddNotePageDto(title = "추가된 노트 페이지 제목", content = "추가된 노트 페이지 내용")
    }

    private fun createTmpUser() {

        // 데이터베이스에 넣을 Member 생성
        val member = Member(email = "test2@example.com", name = "테스트 사용자2")

        // 데이터베이스에 저장
        memberRepository.save(member)

        // 로그인
        val loginDto = LoginDto.Req("test2@example.com", "테스트 사용자2")
        val loginResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )

        // 이후 모든 요청에 들어갈 authorizationHeader 값 얻기
        tmpAuthorizationHeader = loginResultActions.andReturn().response.getHeader("Authorization").toString()
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