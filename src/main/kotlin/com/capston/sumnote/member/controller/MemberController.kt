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

    @DeleteMapping("/withdraw/{email}")
    fun withdraw(@PathVariable("email") email: String): CustomApiResponse<*> {
        return memberService.withdraw(email)
    }
}
