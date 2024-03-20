package com.capston.sumnote.member.service

import com.capston.sumnote.domain.Member
import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.repository.MemberRepository
import com.capston.sumnote.util.exception.CustomValidationException
import com.capston.sumnote.util.exception.EntityDuplicatedException
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

        // 이메일 형식 검증은 checkEmailRegexValid 을 사용하는 것이 아니라
        // Controller 의 @Valid 사용

        // 이메일 검증이 완료된 후의 로직
        return memberRepository.findByEmail(dto.email).let {
            if (!it.isPresent) { // 이메일이 존재하지 않으면 회원가입
                checkEmailDuplicated(dto.email) // 이메일 중복 검증 -> 로직 상 불필요해 보임
                val newMember: Member = dto.toEntity().apply {
                    // 회원가입 시점을 마지막 로그인 시간으로 설정
                    this.lastLoginAt = LocalDateTime.now()
                }
                memberRepository.save(newMember)
                CustomApiResponse.createSuccess(HttpStatus.CREATED.value(), LoginDto.Res(newMember), "회원가입 성공")
            } else { // 이메일이 존재한다면 로그인
                // 기존 회원이 로그인 시 마지막 로그인 시간 업데이트
                it.get().apply {
                    this.lastLoginAt = LocalDateTime.now()
                } . let {
                    updatedMember -> memberRepository.save(updatedMember)
                }
                CustomApiResponse.createSuccess(HttpStatus.OK.value(), LoginDto.Res(it.get()), "로그인 성공")
            }
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
