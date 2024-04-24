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

    @Transactional
    override fun createNote(dto: CreateNoteDto, email: String): CustomApiResponse<*> {

        // 이메일로 멤버 찾기
        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(404, "Member not found")

        // 노트 생성과 저장
        val note = Note(title = dto.note.title, member = member)
        val savedNote = noteRepository.save(note)

        // 노트 페이지 생성과 저장
        dto.notePages.forEach {
            val notePage = NotePage(
                title = it.title,
                content = it.content,
                note = savedNote
            )
            notePageRepository.save(notePage)
        }

        // 응답
        return CustomApiResponse.createSuccessWithoutData<Unit>(201, "노트가 정상적으로 생성되었습니다.")
    }

    override fun findRecentNotesLimited(email: String): CustomApiResponse<List<GetNotesDto>> {
        val notes = noteRepository.findTop5ByMemberEmailOrderByLastModifiedAtDesc(email)
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), notes, "최근 생성된 노트 5개 조회에 성공하였습니다.")
    }

    override fun findAllNotesSorted(email: String): CustomApiResponse<List<GetNotesDto>> {
        val notes = noteRepository.findAllByMemberEmailOrderByLastModifiedAtDesc(email)
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), notes, "모든 노트 조회에 성공하였습니다.")
    }

    override fun getNote(noteId: Long): CustomApiResponse<*> {

        // 노트 찾기 + 예외 처리
        val note = noteRepository.findById(noteId).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "id가 " + noteId + "인 노트는 존재하지 않습니다.")

        // 응답 데이터 만들기
        val noteDetail = NoteDetail(noteId = note.id!!, title = note.title!!)
        val notePagesDetails = note.notePages.map {
            NotePageDetail(
                notePageId = it.id!!,
                title = it.title!!,
                content = it.content!!,
                isQuizExist = it.isQuizExists ?: false
            )
        }

        val getNoteDto = GetNoteDto(note = noteDetail, notePages = notePagesDetails)

        // 응답
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), getNoteDto, "노트 조회에 성공하였습니다.")
    }


}
