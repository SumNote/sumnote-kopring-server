package com.capston.sumnote.member.controller

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.service.MemberServiceImpl
import com.capston.sumnote.util.response.CustomApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/member")
class MemberController(private val memberService: MemberServiceImpl) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: LoginDto.Req, response: HttpServletResponse): CustomApiResponse<LoginDto.Res> {
        val (responseData, token) = memberService.login(dto)
        response.addHeader("Authorization", "Bearer $token")
        return CustomApiResponse.createSuccess(HttpStatus.OK.value(), responseData, "로그인 성공")
    }

    @DeleteMapping("/withdraw/{email}")
    fun withdraw(@PathVariable("email") email: String): CustomApiResponse<*> {
        return memberService.withdraw(email)
    }
}