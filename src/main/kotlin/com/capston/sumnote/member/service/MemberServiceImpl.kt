package com.capston.sumnote.member.service

import com.capston.sumnote.domain.Member
import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.util.exception.CustomValidationException
import com.capston.sumnote.util.exception.EntityDuplicatedException
import com.capston.sumnote.util.exception.AutoLoginDeactivateException
import com.capston.sumnote.util.response.CustomApiResponse
import com.capston.sumnote.util.valid.CustomValid
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberServiceImpl(private val memberRepository: MemberRepository) : MemberService{

    // 로그인
    @Transactional
    override fun login(dto: LoginDto.Req): CustomApiResponse<LoginDto.Res> {

        // 이메일을 통해 사용자 정보를 조회
        val optionalMember = memberRepository.findByEmail(dto.email)

        // 사용자 정보가 존재하면 활성 상태를 확인
        if (optionalMember.isPresent) {
            val member = optionalMember.get()

            // 계정이 비활성화된 경우 예외 발생
            if (!member.isAutoLoginActive) {
                throw AutoLoginDeactivateException("자동 로그아웃 되었습니다. 다시 로그인 해주세요.")
            }

            // 사용자의 마지막 로그인 시간 업데이트
            member.apply {
                this.lastLoginAt = LocalDateTime.now()
            }.also {
                memberRepository.save(it)
            }

            // 로그인 성공 응답을 반환
            return CustomApiResponse.createSuccess(HttpStatus.OK.value(), LoginDto.Res(member), "로그인 성공")
        } else {
            // 사용자 정보가 없으면 회원가입
            checkEmailDuplicated(dto.email) // 이메일 중복 검증
            val newMember: Member = dto.toEntity().apply {
                // 회원가입 시점을 마지막 로그인 시간으로 설정
                this.lastLoginAt = LocalDateTime.now()
                this.isAutoLoginActive = true // 새 계정을 활성 상태로 설정
            }
            memberRepository.save(newMember)
            // 회원가입 성공 응답을 반환
            return CustomApiResponse.createSuccess(HttpStatus.CREATED.value(), LoginDto.Res(newMember), "회원가입 성공")
        }
    }


    // 회원탈퇴
    @Transactional
    override fun withdraw(email: String): CustomApiResponse<*> {

        checkEmailRegexValid(email) // 이메일 형식

        val foundMember: Optional<Member> = memberRepository.findByEmail(email)
        return if (foundMember.isPresent) {
            memberRepository.delete(foundMember.get())
            CustomApiResponse.createSuccess(202, null, "회원탈퇴 성공")
        } else {
            CustomApiResponse.createFailWithoutData(404, "존재하지 않는 이메일입니다.")
        }
    }

    private fun checkEmailRegexValid(email: String) {
        // 이메일 형식 검증
        if (!CustomValid.isEmailRegexValid(email)) {
            throw CustomValidationException("이메일 형식을 맞춰주세요.")
        }
    }

    private fun checkEmailDuplicated(email: String) {
        // 이메일 중복 검증
        val emailExists = memberRepository.findByEmail(email).isPresent
        if (emailExists) {
            throw EntityDuplicatedException("이미 사용중인 이메일입니다.")
        }
    }

}
