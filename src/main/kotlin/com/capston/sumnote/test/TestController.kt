package com.capston.sumnote.test

import com.capston.sumnote.util.response.CustomApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/api/test/authenticated")
    fun testAuthenticated(): CustomApiResponse<String> {
        return CustomApiResponse.createSuccess(200, "OK", "Request successful")
    }

}