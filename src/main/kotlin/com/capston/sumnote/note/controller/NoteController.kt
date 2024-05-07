package com.capston.sumnote.note.controller

import com.capston.sumnote.note.dto.AddNotePageDto
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

    private fun getCurrentUserEmail(): String =
        (SecurityContextHolder.getContext().authentication.principal as? User)?.username
            ?: "사용자를 찾을 수 없습니다."

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {
        val response = noteService.createNote(dto, getCurrentUserEmail())
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getNotes(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {
        val response = noteService.findNotesByType(getCurrentUserEmail(), type)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{noteId}")
    fun getNote(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.getNote(getCurrentUserEmail(), noteId)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @PutMapping("{noteId}/title")
    fun changeNoteTitle(@PathVariable("noteId") noteId: Long, @RequestBody dto: ChangeTitleDto): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.changeTitle(getCurrentUserEmail(), noteId, dto)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @PutMapping("{noteId}/add")
    fun addNotePage(@PathVariable("noteId") noteId: Long, @RequestBody dto: AddNotePageDto): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.addNotePage(getCurrentUserEmail(), noteId, dto)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @DeleteMapping("{noteId}")
    fun deleteNoteAndQuiz(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.deleteNoteAndQuiz(getCurrentUserEmail(), noteId)
        return ResponseEntity.status(responseData.status).body(responseData)
    }
}