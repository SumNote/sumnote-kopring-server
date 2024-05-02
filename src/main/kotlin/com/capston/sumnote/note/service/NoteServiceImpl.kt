package com.capston.sumnote.note.service

import com.capston.sumnote.domain.Member
import com.capston.sumnote.domain.Note
import com.capston.sumnote.domain.NotePage
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.dto.*
import com.capston.sumnote.note.repository.NoteRepository
import com.capston.sumnote.note.repository.NotePageRepository
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
    override fun createNote(dto: CreateNoteDto, email: String): CustomApiResponse<*> {
        val member = getMember(email) ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")
        val note = Note(title = dto.note.title, member = member)
        val savedNote = noteRepository.save(note)
        dto.notePages.forEach {
            notePageRepository.save(NotePage(title = it.title, content = it.content, note = savedNote))
        }

        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccessWithoutData<Unit>(HttpStatus.CREATED.value(), "노트가 정상적으로 생성되었습니다.")
    }

    /**
     * 노트 목록 조회
     */
    override fun findNotesByType(email: String, type: String): CustomApiResponse<*> = when (type) {
        "home" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), noteRepository.findTop5ByMemberEmailOrderByLastModifiedAtDesc(email), "최근 생성된 노트 5개 조회에 성공하였습니다.")
        "all" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), noteRepository.findAllByMemberEmailOrderByLastModifiedAtDesc(email), "모든 노트 조회에 성공하였습니다.")
        else -> CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), "type은 home 또는 all 이어야 합니다.")
    }

    /**
     * 특정 노트 조회
     */
    override fun getNote(email: String, noteId: Long): CustomApiResponse<*> {

        // 노트 검증 (1. 노트가 존재하는지, 2. 노트가 사용자의 것인지)
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        // 노트 검증을 성공적으로 통과한 후, 노트의 상세 정보를 생성하여 반환
        val noteDetail = NoteDetail(noteId = note!!.id!!, title = note.title!!)
        val notePagesDetails = note.notePages.map {
            NotePageDetail(notePageId = it.id!!, title = it.title!!, content = it.content!!)
        }

        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccess(
            HttpStatus.OK.value(),
            GetNoteDto(note = noteDetail, notePages = notePagesDetails),
            "노트 조회에 성공하였습니다."
        )
    }

    /**
     * 노트 제목 수정
     */
    @Transactional
    override fun changeTitle(email: String, noteId: Long, dto: ChangeTitleDto): CustomApiResponse<*> {

        // 노트 검증 (1. 노트가 존재하는지, 2. 노트가 사용자의 것인지)
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        // 노트 검증을 성공적으로 통과한 후, 노트 제목 수정
        note!!.updateTitle(dto)
        noteRepository.save(note)

        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.OK.value(), "노트 제목이 정상적으로 변경되었습니다."
        )
    }

    /**
     * 노트에 페이지 추가
     */
    @Transactional
    override fun addNotePage(email: String, noteId: Long, dto: AddNotePageDto): CustomApiResponse<*> {

        // 노트 검증 (1. 노트가 존재하는지, 2. 노트가 사용자의 것인지)
        val (note, errorResponse) = verifyNoteOwnership(email, noteId)
        if (errorResponse != null) {
            return errorResponse
        }

        // 노트 검증을 성공적으로 통과한 후, 노트 페이지 추가
        val newNotePage = NotePage(title = dto.title, content = dto.content, note = note)
        notePageRepository.save(newNotePage)

        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccessWithoutData<Unit>(
            HttpStatus.OK.value(), "페이지가 정상적으로 추가되었습니다."
        )
    }

    /**
     * 공통 로직
     */
    private fun verifyNoteOwnership(email: String, noteId: Long): Pair<Note?, CustomApiResponse<*>?> {

        // 1. 노트가 존재하는지 확인
        val note = noteRepository.findById(noteId).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.NOT_FOUND.value(),
                "id가 ${noteId}인 노트는 존재하지 않습니다."
            )

        // 사용자가 존재하는지 확인 (Filter 단에서 검증되긴 하는데, 사용자를 찾기 위해 해당 로직 작성함)
        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.NOT_FOUND.value(),
                "사용자를 찾을 수 없습니다."
            )

        // 2. 노트가 사용자의 것인지 확인
        if (note.member != member) {
            return null to CustomApiResponse.createFailWithoutData(
                HttpStatus.FORBIDDEN.value(),
                "접근할 수 없는 노트입니다."
            )
        }

        return note to null
    }

    // 사용자 찾기
    private fun getMember(email: String): Member? =
        memberRepository.findByEmail(email).orElse(null)
}