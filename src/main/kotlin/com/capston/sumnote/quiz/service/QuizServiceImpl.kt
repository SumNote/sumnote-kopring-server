package com.capston.sumnote.quiz.service

import com.capston.sumnote.domain.*
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.capston.sumnote.quiz.dto.*
import com.capston.sumnote.quiz.repository.QuizPageRepository
import com.capston.sumnote.quiz.repository.QuizRepository
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class QuizServiceImpl(
    private val memberRepository: MemberRepository,
    private val noteRepository: NoteRepository,
    private val quizRepository: QuizRepository,
    private val quizPageRepository: QuizPageRepository
) : QuizService {

    @Transactional
    override fun createQuiz(email: String, dto: CreateQuizDto): CustomApiResponse<*> {
        val member = getMember(email) ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")
        val note = noteRepository.findById(dto.noteId).orElse(null)
            ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "노트를 찾을 수 없습니다.")

        val quiz = note.quiz ?: Quiz(title = dto.title, member = member).also {
            note.quiz = it
            quizRepository.save(it)
        }

        dto.quiz.forEach { quizInfo ->
            val quizPage = QuizPage(question = quizInfo.question, answer = quizInfo.answer, commentary = quizInfo.commentary, quiz = quiz)
            quizPageRepository.save(quizPage)
            quizInfo.selection.forEach { selectionText ->
                val selection = Selection(selection = selectionText, quizPage = quizPage)
                quizPage.selections.add(selection)
            }
        }

        return CustomApiResponse.createSuccessWithoutData<Unit>(HttpStatus.CREATED.value(), "퀴즈가 정상적으로 생성되었습니다.")
    }

    override fun getQuiz(email: String, type: String): CustomApiResponse<*> = when(type){
        "home" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), quizRepository.findQuizAtHome(email), "최근 생성된 퀴즈 5개 조회에 성공하였습니다.")
        "all" -> CustomApiResponse.createSuccess(HttpStatus.OK.value(), quizRepository.findQuizAtAll(email), "모든 퀴즈 조회에 성공하였습니다.")
        else -> CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), "type은 home 또는 all 이어야 합니다.")
    }

    override fun getOneQuiz(email: String, quizId: Long): CustomApiResponse<*> {
        val (quiz, errorResponse) = verifyNoteOwnership(email, quizId)
        if (errorResponse != null) {
            return errorResponse
        }

        val quizPagesDetails = quiz?.quizPages?.map {
            val selectionList: MutableList<PageSelection> = mutableListOf()
            it.selections.forEach { selection ->
                selectionList.add(PageSelection(selection = selection.selection))
            }
            QuizPageInfo(quizPageId = it.id!!, question = it.question, answer = it.answer, commentary = it.commentary, selection = selectionList)
        }

        return CustomApiResponse.createSuccess(
            HttpStatus.OK.value(),
            GetQuizDto(quizId = quiz?.id, quiz = quizPagesDetails),
            "퀴즈 조회에 성공하였습니다."
        )
    }

    private fun verifyNoteOwnership(email: String, quizId: Long): Pair<Quiz?, CustomApiResponse<*>?> {
        val quiz = quizRepository.findById(quizId).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "id가 ${quizId}인 노트는 존재하지 않습니다.")

        val member = memberRepository.findByEmail(email).orElse(null)
            ?: return null to CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")

        if (quiz.member != member) {
            return null to CustomApiResponse.createFailWithoutData(HttpStatus.FORBIDDEN.value(), "접근할 수 없는 노트입니다.")
        }

        return quiz to null
    }

    private fun getMember(email: String): Member? = memberRepository.findByEmail(email).orElse(null)
}
