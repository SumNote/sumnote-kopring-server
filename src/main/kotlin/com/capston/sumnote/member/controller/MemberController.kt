package com.capston.sumnote.member.controller

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.service.MemberServiceImpl
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/member")
class MemberController(private val memberService: MemberServiceImpl) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: LoginDto.Req): CustomApiResponse<LoginDto.Res> {
        return memberService.login(dto)
    }

    @PostMapping("/re-login")
    fun reLogin(@Valid @RequestBody dto: LoginDto.Req) : CustomApiResponse<LoginDto.Res> {
        return memberService.reLogin(dto)
    }

    @DeleteMapping("/withdraw/{email}")
    fun withdraw(@PathVariable("email") email: String): CustomApiResponse<*> {
        return memberService.withdraw(email)
    }
}
