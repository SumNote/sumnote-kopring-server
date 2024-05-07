package com.capston.sumnote.note.controller

import com.capston.sumnote.note.dto.AddNotePageDto
import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.note.service.NoteService
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.security.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sum-note")
class NoteController(private val noteService: NoteService) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {
        val response = noteService.createNote(SecurityUtils.getCurrentUserEmail(), dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getNotes(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {
        val response = noteService.findNotesByType(SecurityUtils.getCurrentUserEmail(), type)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{noteId}")
    fun getNote(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.getNote(SecurityUtils.getCurrentUserEmail(), noteId)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @PutMapping("{noteId}/title")
    fun changeNoteTitle(@PathVariable("noteId") noteId: Long, @RequestBody dto: ChangeTitleDto): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.changeTitle(SecurityUtils.getCurrentUserEmail(), noteId, dto)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @PutMapping("{noteId}/add")
    fun addNotePage(@PathVariable("noteId") noteId: Long, @RequestBody dto: AddNotePageDto): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.addNotePage(SecurityUtils.getCurrentUserEmail(), noteId, dto)
        return ResponseEntity.status(responseData.status).body(responseData)
    }

    @DeleteMapping("{noteId}")
    fun deleteNoteAndQuiz(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val responseData = noteService.deleteNoteAndQuiz(SecurityUtils.getCurrentUserEmail(), noteId)
        return ResponseEntity.status(responseData.status).body(responseData)
    }
}