package com.capston.sumnote.member.service

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.util.exception.CustomValidationException
import com.capston.sumnote.util.security.jwt.JwtTokenProvider
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.valid.CustomValid
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider
) : MemberService {

        // 로그인
        @Transactional
        override fun login(dto: LoginDto.Req): CustomApiResponse<LoginDto.Res> {
            val member = memberRepository.findByEmail(dto.email).orElseGet {
                memberRepository.save(dto.toEntity()) // 사용자 정보가 없으면 회원가입 진행
            }

            // JWT 토큰 생성
            val token = jwtTokenProvider.createToken(member.email.toString(), listOf("ROLE_USER"))

            // 로그인 응답에 토큰 포함하여 반환
            val response = LoginDto.Res(member.email.toString(), member.name.toString(), token)
            return CustomApiResponse.createSuccess(HttpStatus.OK.value(), response, "로그인 성공")
        }

    @Transactional
    override fun withdraw(email: String): CustomApiResponse<*> {
        checkEmailRegexValid(email)

        val member = memberRepository.findByEmail(email)
            .orElseThrow { CustomValidationException("존재하지 않는 이메일입니다.") }

        memberRepository.delete(member)
        return CustomApiResponse.createSuccess(202, null, "회원탈퇴 성공")
    }

    private fun checkEmailRegexValid(email: String) {
        if (!CustomValid.isEmailRegexValid(email)) {
            throw CustomValidationException("이메일 형식을 맞춰주세요.")
        }
    }

}

