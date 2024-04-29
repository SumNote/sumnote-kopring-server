package com.capston.sumnote.quiz.service

import com.capston.sumnote.domain.*
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.note.repository.NotePageRepository
import com.capston.sumnote.note.repository.NoteRepository
import com.capston.sumnote.quiz.dto.CreateQuizDto
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
        private val quizRepository: QuizRepository,
        private val quizPageRepository: QuizPageRepository
) : QuizService{

    @Transactional
    override fun createQuiz(dto: CreateQuizDto, email: String): CustomApiResponse<*> {

        val member = getMember(email) ?: return CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다.")

        val quiz = Quiz(title = dto.title, member = member)
        val savedQuiz = quizRepository.save(quiz)
        dto.quiz.forEach {
            val quizPage = QuizPage(question = it.question, answer = it.answer, commentary = it.commentary, quiz = savedQuiz)
            val savedQuizPage = quizPageRepository.save(quizPage)

            for (s in it.selection) {
                quizPage.addSelection(Selection(selection = s, quizPage = savedQuizPage))
            }
        }

        // ResponseBody 에 포함될 데이터
        return CustomApiResponse.createSuccessWithoutData<Unit>(HttpStatus.CREATED.value(), "퀴즈가 정상적으로 생성되었습니다.")
    }

    // 사용자 찾기
    private fun getMember(email: String): Member? =
            memberRepository.findByEmail(email).orElse(null)

}