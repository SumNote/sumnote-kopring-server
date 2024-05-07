package com.capston.sumnote.note.service

import com.capston.sumnote.domain.Note
import com.capston.sumnote.domain.NotePage
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.dto.*
import com.capston.sumnote.note.repository.NotePageRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.capston.sumnote.util.exception.EntityNotFoundException
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NoteServiceImpl(
    private val memberRepository: MemberRepository,
    private val noteRepository: NoteRepository,
    private val notePageRepository: NotePageRepository
) : NoteService {

    /**
     * 노트 생성
     */
    @Transactional
    override fun createNote(email: String, dto: CreateNoteDto): CustomApiResponse<*> {
        val member = memberRepository.findByEmail(email).orElseThrow {
            throw EntityNotFoundException("사용자를 찾을 수 없습니다.")
        }

        val note = Note(title = dto.note.title, member = member)

        dto.notePages.forEach {
            val notePage = NotePage(title = it.title, content = it.content, note = note)
            note.notePages.add(notePage)
        }

        noteRepository.save(note)
        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.CREATED.value(),
            "노트가 정상적으로 생성되었습니다."
        )
    }

    /**
     * 노트 리스트
     */
    override fun findNotesByType(email: String, type: String): CustomApiResponse<*> = when (type) {
        "home" -> CustomApiResponse.createSuccess(
            HttpStatus.OK.value(),
            noteRepository.findNoteAtHome(email),
            "최근 생성된 노트 5개 조회에 성공하였습니다."
        )
        "all" -> CustomApiResponse.createSuccess(
            HttpStatus.OK.value(),
            noteRepository.findNoteAtAll(email),
            "모든 노트 조회에 성공하였습니다."
        )
        else -> CustomApiResponse.createFailWithoutData(
            HttpStatus.BAD_REQUEST.value(),
            "type은 home 또는 all 이어야 합니다."
        )
    }

    /**
     * 특정 노트 모든 페이지 조회
     */
    override fun getNote(email: String, noteId: Long): CustomApiResponse<*> {
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        val noteDetail = NoteDetail(noteId = note!!.id!!, title = note.title!!)
        val notePagesDetails = note.notePages.map {
            NotePageDetail(notePageId = it.id!!, title = it.title!!, content = it.content!!)
        }

        return CustomApiResponse.createSuccess(
            HttpStatus.OK.value(),
            GetNoteDto(note = noteDetail, notePages = notePagesDetails),
            "노트 조회에 성공하였습니다."
        )
    }

    /**
     * 노트 이름 변경
     */
    @Transactional
    override fun changeTitle(email: String, noteId: Long, dto: ChangeTitleDto): CustomApiResponse<*> {
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        note!!.updateTitle(dto)
        noteRepository.save(note)

        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.OK.value(),
            "노트 제목이 정상적으로 변경되었습니다."
        )
    }

    /**
     * 특정 노트에 페이지 추가
     */
    @Transactional
    override fun addNotePage(email: String, noteId: Long, dto: AddNotePageDto): CustomApiResponse<*> {
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        val newNotePage = NotePage(title = dto.title, content = dto.content, note = note)
        notePageRepository.save(newNotePage)

        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.OK.value(),
            "페이지가 정상적으로 추가되었습니다."
        )
    }

    /**
     * 노트 삭제
     */
    @Transactional
    override fun deleteNoteAndQuiz(email: String, noteId: Long): CustomApiResponse<*> {
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        note?.let { noteRepository.delete(it) }
        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.OK.value(),
            "노트가 정상적으로 삭제되었습니다."
        )
    }

    /**
     * 노트 소유권 확인
     */
    private fun verifyNoteOwnership(email: String, noteId: Long): Pair<Note?, CustomApiResponse<*>?> {
        val note = noteRepository.findById(noteId).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.NOT_FOUND.value(),
                "id가 ${noteId}인 노트는 존재하지 않습니다."
            )

        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.NOT_FOUND.value(),
                "사용자를 찾을 수 없습니다."
            )

        if (note.member != member) {
            return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.FORBIDDEN.value(),
                "접근할 수 없는 노트입니다."
            )
        }

        return note to null
    }
}
