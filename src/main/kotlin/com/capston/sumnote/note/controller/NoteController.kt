package com.capston.sumnote.note.controller

import com.capston.sumnote.note.dto.AddNotePageDto
import com.capston.sumnote.note.dto.ChangeTitleDto
import com.capston.sumnote.note.dto.CreateNoteDto
import com.capston.sumnote.note.service.NoteService
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.security.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sum-note")
class NoteController(private val noteService: NoteService) {

    @PostMapping
    fun createNote(@RequestBody dto: CreateNoteDto): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.createNote(userEmail, dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping
    fun getNotes(@RequestParam("type") type: String): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.findNotesByType(userEmail, type)
        return ResponseEntity.status(response.status).body(response)
    }

    @GetMapping("{noteId}")
    fun getNote(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.getNote(userEmail, noteId)
        return ResponseEntity.status(response.status).body(response)
    }

    @PutMapping("{noteId}/title")
    fun changeNoteTitle(@PathVariable("noteId") noteId: Long, @RequestBody dto: ChangeTitleDto): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.changeTitle(userEmail, noteId, dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @PutMapping("{noteId}/add")
    fun addNotePage(@PathVariable("noteId") noteId: Long, @RequestBody dto: AddNotePageDto): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.addNotePage(userEmail, noteId, dto)
        return ResponseEntity.status(response.status).body(response)
    }

    @DeleteMapping("{noteId}")
    fun deleteNoteAndQuiz(@PathVariable("noteId") noteId: Long): ResponseEntity<CustomApiResponse<*>> {
        val userEmail = SecurityUtils.getCurrentUserEmail()
        val response = noteService.deleteNoteAndQuiz(userEmail, noteId)
        return ResponseEntity.status(response.status).body(response)
    }
}
