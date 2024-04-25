package com.capston.sumnote.note.service

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
        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")

        val note = Note(title = dto.note.title, member = member)
        val savedNote = noteRepository.save(note)
        dto.notePages.forEach {
            notePageRepository.save(NotePage(title = it.title, content = it.content, note = savedNote))
        }
        return CustomApiResponse.createSuccessWithoutData<Unit>(HttpStatus.CREATED.value(), "노트가 정상적으로 생성되었습니다.")
    }

    /**
    * 모든 노트 조회
     */
    override fun findNotesByType(email: String, type: String): CustomApiResponse<*> = when (type) {
        "home" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), noteRepository.findTop5ByMemberEmailOrderByLastModifiedAtDesc(email), "최근 생성된 노트 5개 조회에 성공하였습니다.")
        "all" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), noteRepository.findAllByMemberEmailOrderByLastModifiedAtDesc(email), "모든 노트 조회에 성공하였습니다.")
        else -> CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), "type은 home 또는 all 이어야 합니다.")
    }

    /**
     * 노트 내용 조회
     */
    override fun getNote(email: String, noteId: Long): CustomApiResponse<*> { // TODO : 접근할 수 없는 노트
        val note = noteRepository.findById(noteId).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "id가 " + noteId + "인 노트는 존재하지 않습니다.")
        val noteDetail = NoteDetail(noteId = note.id!!, title = note.title!!)
        val notePagesDetails = note.notePages.map {
            NotePageDetail(notePageId = it.id!!, title = it.title!!, content = it.content!!, isQuizExist = it.isQuizExists ?: false)
        }
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), GetNoteDto(note = noteDetail, notePages = notePagesDetails), "노트 조회에 성공하였습니다.")
    }

    @Transactional
    override fun changeTitle(email: String, noteId: Long, dto: ChangeTitleDto): CustomApiResponse<*> {
        // 노트 찾기
        val note = noteRepository.findById(noteId).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "id가 " + noteId + "인 노트는 존재하지 않습니다.")

        // 이메일로 사용자 찾기
        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")

        // 사용자가 해당 노트를 갖고있는지 확인
        if (note.member != member) {
            return CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), "접근할 수 없는 노트입니다.")
        }

        // 업데이트
        note.updateTitle(dto)

        // 응답
        return CustomApiResponse.createSuccessWithoutData<Unit>(HttpStatus.OK.value(), "노트 제목이 정상적으로 변경되었습니다.")
    }


}
