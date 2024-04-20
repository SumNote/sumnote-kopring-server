package com.capston.sumnote.member.service

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.util.exception.CustomValidationException
import com.capston.sumnote.util.exception.EntityDuplicatedException
import com.capston.sumnote.util.security.jwt.JwtTokenProvider
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.valid.CustomValid
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider
) : MemberService {

    // 로그인
    @Transactional
    override fun login(dto: LoginDto.Req): CustomApiResponse<LoginDto.Res> =
        processLogin(dto, allowReactivation = false)

    // 로그인
    private fun processLogin(dto: LoginDto.Req, allowReactivation: Boolean): CustomApiResponse<LoginDto.Res> {
        val member = memberRepository.findByEmail(dto.email).orElseGet {

            // 사용자 정보가 없으면 회원가입 진행
            val newMember = dto.toEntity().apply {
                lastLoginAt = LocalDateTime.now()
            }
            memberRepository.save(newMember)
            return@orElseGet newMember
        }

        memberRepository.save(member)

        // JWT 토큰 생성
        val token = jwtTokenProvider.createToken(member.email.toString(), listOf("ROLE_USER")) // 역할은 예시입니다.

        // 로그인 응답에 토큰 포함하여 반환
        val response = LoginDto.Res(member.email.toString(), member.name.toString(), token) // 수정된 생성자를 사용
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), response, if (allowReactivation) "재로그인 성공" else "로그인 성공")

    }

    @Transactional
    override fun withdraw(email: String): CustomApiResponse<*> {
        checkEmailRegexValid(email)

        memberRepository.findByEmail(email).ifPresentOrElse({ member ->
            memberRepository.delete(member)
            CustomApiResponse.createSuccess(202, null, "회원탈퇴 성공")
        }, {
            throw CustomValidationException("존재하지 않는 이메일입니다.")
        })

        return CustomApiResponse.createFailWithoutData(404, "존재하지 않는 이메일입니다.")
    }

    private fun checkEmailRegexValid(email: String) {
        if (!CustomValid.isEmailRegexValid(email)) {
            throw CustomValidationException("이메일 형식을 맞춰주세요.")
        }
    }

    private fun checkEmailDuplicated(email: String) {
        memberRepository.findByEmail(email).ifPresent {
            throw EntityDuplicatedException("이미 사용중인 이메일입니다.")
        }
    }
}

