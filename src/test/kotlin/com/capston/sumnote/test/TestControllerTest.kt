package com.capston.sumnote.test

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun simpleTest() {
        val result = mockMvc.get("/api/test")
            .andExpect {
                status { isOk() }
                content { string("test success (add webhook)") }
            }
            .andReturn()

        val responseContent = result.response.contentAsString
        assertEquals("test success (add webhook)", responseContent)
    }
}