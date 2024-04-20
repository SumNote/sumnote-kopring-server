package com.capston.sumnote.note.controller

import com.capston.sumnote.member.service.NoteService
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.security.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sum-note")
class NoteController(
    private val noteService: NoteService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto, request: HttpServletRequest): ResponseEntity<CustomApiResponse<*>> {

        val token = jwtTokenProvider.resolveToken(request)
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."))
        }

        val email = jwtTokenProvider.getEmailFromToken(token)
            ?: return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), "토큰을 통해 이메일을 찾을 수 없습니다."))

        val response = noteService.createNote(dto, email) // 응답 데이터
        return ResponseEntity.status(HttpStatus.CREATED).body(response) // 상태 코드 변경
    }
}