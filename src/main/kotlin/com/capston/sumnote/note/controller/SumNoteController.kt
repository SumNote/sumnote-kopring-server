package com.capston.sumnote.note.controller

import com.capston.sumnote.note.service.NoteService
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/sum-note")
class SumNoteController(private val noteService: NoteService) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 가져오기
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        val email = if (principal is User) {
            principal.username
        } else {
            principal.toString()
        }

        // 응답
        val response = noteService.createNote(dto, email)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getNotes(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 가져오기
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        val email = if (principal is User) {
            principal.username
        } else {
            principal.toString()
        }

        return when (type) {
            "home" -> ResponseEntity.ok(noteService.findRecentNotesLimited(email))
            "all" -> ResponseEntity.ok(noteService.findAllNotesSorted(email))
            else -> ResponseEntity.badRequest().body(CustomApiResponse.createFailWithoutData(400, "Invalid type parameter"))
        }
    }

    @GetMapping ("{noteId}")
    fun getNote(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {

        // 헤더에 포함된 토큰으로 이메일 값 가져오기
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        val email = if (principal is User) {
            principal.username
        } else {
            principal.toString()
        }

        // 응답
        val response = noteService.getNote(noteId)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
