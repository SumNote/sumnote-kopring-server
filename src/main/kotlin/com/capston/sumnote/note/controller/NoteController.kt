package com.capston.sumnote.note.controller

import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.note.service.NoteService
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sum-note")
class NoteController(private val noteService: NoteService) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 찾기
        val email = (SecurityContextHolder.getContext().authentication.principal as? User)?.username

        // 응답 데이터
        val response = noteService.createNote(dto, email ?: "사용자를 찾을 수 없습니다.")

        // 응답
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getNotes(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 찾기
        val email = (SecurityContextHolder.getContext().authentication.principal as? User)?.username

        // 응답 데이터
        val response = noteService.findNotesByType(email ?: "사용자를 찾을 수 없습니다.", type)

        // 응답
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping ("{noteId}")
    fun getNote(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 찾기
        val email = (SecurityContextHolder.getContext().authentication.principal as? User)?.username

        // 응답 데이터
        val responseData = noteService.getNote(email ?: "사용자를 찾을 수 없습니다.", noteId)

        // 응답
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @PutMapping("{noteId}/title")
    fun changeNoteTitle(@PathVariable("noteId") noteId: Long, @RequestBody dto: ChangeTitleDto): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 찾기
        val email = (SecurityContextHolder.getContext().authentication.principal as? User)?.username

        // 응답 데이터
        val responseData = noteService.changeTitle(email ?: "사용자를 찾을 수 없습니다.", noteId, dto)

        // 응답
        return ResponseEntity.status(responseData.status).body(responseData)
    }
}
