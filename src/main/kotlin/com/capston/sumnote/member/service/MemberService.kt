package com.capston.sumnote.member.service

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.util.response.CustomApiResponse

interface MemberService {
    fun login(dto: LoginDto.Req): CustomApiResponse<LoginDto.Res>
    fun reLogin(dto: LoginDto.Req): CustomApiResponse<LoginDto.Res>
    fun withdraw(email: String): CustomApiResponse<*>
}