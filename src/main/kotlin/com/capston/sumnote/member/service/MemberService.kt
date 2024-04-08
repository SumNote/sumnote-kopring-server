package com.capston.sumnote.member.service

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.util.response.CustomApiResponse

interface MemberService {
    fun login(dto: LoginDto.Req): Pair<LoginDto.Res, String>
    fun withdraw(email: String): CustomApiResponse<*>
}
