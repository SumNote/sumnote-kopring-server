package com.capston.sumnote.member.controller

import com.capston.sumnote.member.dto.LoginDto
import com.capston.sumnote.member.service.MemberServiceImpl
import com.capston.sumnote.util.response.CustomApiResponse
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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