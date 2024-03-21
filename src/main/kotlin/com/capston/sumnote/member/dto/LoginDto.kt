package com.capston.sumnote.member.dto

import com.capston.sumnote.domain.Member
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class LoginDto {

    data class Req(
        @field:Email(message = "이메일 형식을 맞춰주세요.")
        @field:NotBlank(message = "빈 값일 수 없습니다.")
        val email: String,

        @field:NotBlank(message = "빈 값일 수 없습니다.")
        val name: String
    ) {
        fun toEntity(): Member {
            return Member(
                email = this.email,
                name = this.name
            )
        }
    }

    data class Res(val email: String, val name: String, val token: String) {
        constructor(member: Member, token: String) : this(email = member.email.toString(), name = member.name.toString(), token = token)
    }
}
