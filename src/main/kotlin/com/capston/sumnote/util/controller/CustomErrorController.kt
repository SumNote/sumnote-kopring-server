package com.capston.sumnote.util.controller

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): ResponseEntity<CustomApiResponse<Any?>> {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as? Int
        status?.let {
            return when (it) {
                HttpStatus.NOT_FOUND.value() -> ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse(HttpStatus.NOT_FOUND.value(), null, "요청 경로를 찾을 수 없습니다."))
                HttpStatus.BAD_REQUEST.value() -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse(HttpStatus.BAD_REQUEST.value(), null, "잘못된 요청입니다."))
                HttpStatus.FORBIDDEN.value() -> ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse(HttpStatus.FORBIDDEN.value(), null, "접근이 금지되었습니다."))
                HttpStatus.METHOD_NOT_ALLOWED.value() -> ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(CustomApiResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), null, "허용되지 않는 메소드입니다."))
                else -> ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "내부 서버 오류가 발생했습니다."))
            }
        }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(CustomApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "내부 서버 오류가 발생했습니다."))
    }
}