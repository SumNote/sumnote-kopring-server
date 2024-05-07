package com.capston.sumnote.quiz.service

import com.capston.sumnote.domain.*
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.capston.sumnote.quiz.dto.CreateQuizDto
import com.capston.sumnote.quiz.dto.GetQuizDto
import com.capston.sumnote.quiz.dto.PageSelection
import com.capston.sumnote.quiz.dto.QuizPageInfo
import com.capston.sumnote.quiz.repository.QuizPageRepository
import com.capston.sumnote.quiz.repository.QuizRepository
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional(readOnly = true)
class QuizServiceImpl(
        private val memberRepository: MemberRepository,
        private val noteRepository: NoteRepository,
        private val quizRepository: QuizRepository,
        private val quizPageRepository: QuizPageRepository
) : QuizService{

    @Transactional
    override fun createQuiz(email: String, dto: CreateQuizDto): CustomApiResponse<*> {
        val member = getMember(email) ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")

        val note = noteRepository.findById(dto.noteId).orElse(null)
                ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "노트를 찾을 수 없습니다.") // 노트를 찾음

        // 노트가 퀴즈를 가지고 있는지 확인하고 가지고 있지 않으면 새로운 퀴즈 생성
        val quiz = note.quiz ?: Quiz(title = dto.title, member = member).also {
            // 노트에 새로운 퀴즈 연결
            note.quiz = it
            quizRepository.save(it)
        }

        // 퀴즈 페이지 및 선택지들 생성 및 추가
        dto.quiz.forEach { quizInfo ->
            val quizPage = QuizPage(question = quizInfo.question, answer = quizInfo.answer, commentary = quizInfo.commentary, quiz = quiz)
            quizPageRepository.save(quizPage) // QuizPage 저장

            quizInfo.selection.forEach { selectionText ->
                val selection = Selection(selection = selectionText, quizPage = quizPage)
                quizPage.selections.add(selection) // Selection 리스트에 추가
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
        // 퀴즈 검증 (1. 퀴즈가 존재하는지, 2. 퀴즈가 사용자의 것인지)
        val (quiz, errorResponse) = verifyNoteOwnership(email, quizId)
        if (errorResponse != null) {
            return errorResponse
        }

        // 퀴즈 검증을 성공적으로 통과한 후, 퀴즈의 상세 정보를 생성하여 반환
        val quizPagesDetails = quiz?.quizPages?.map {

            val selectionList : MutableList<PageSelection> = mutableListOf()
            it.selections.map {
                selectionList.add(PageSelection(selection = it.selection))
            }
            QuizPageInfo(quizPageId = it.id!!, question = it.question, answer = it.answer, commentary = it.commentary, selection = selectionList)
        }
        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccess(
                HttpStatus.OK.value(),
                GetQuizDto(quizId = quiz?.id, quiz = quizPagesDetails),
                "퀴즈 조회에 성공하였습니다."
        )
    }

    /**
     * 공통 로직
     */
    private fun verifyNoteOwnership(email: String, quizId: Long): Pair<Quiz?, CustomApiResponse<*>?> {

        // 1. 퀴즈가 존재하는지 확인
        val quiz = quizRepository.findById(quizId).orElse(null)
                ?: return null to CustomApiResponse.createFailWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        "id가 ${quizId}인 노트는 존재하지 않습니다."
                )

        // 사용자가 존재하는지 확인 (Filter 단에서 검증되긴 하는데, 사용자를 찾기 위해 해당 로직 작성함)
        val member = memberRepository.findByEmail(email).orElse(null)
                ?: return null to CustomApiResponse.createFailWithoutData(
                        HttpStatus.NOT_FOUND.value(),
                        "사용자를 찾을 수 없습니다."
                )

        // 2. 퀴즈가 사용자의 것인지 확인
        if (quiz.member != member) {
            return null to CustomApiResponse.createFailWithoutData(
                    HttpStatus.FORBIDDEN.value(),
                    "접근할 수 없는 노트입니다."
            )
        }

        return quiz to null
    }

    // 사용자 찾기
    private fun getMember(email: String): Member? =
            memberRepository.findByEmail(email).orElse(null)

}