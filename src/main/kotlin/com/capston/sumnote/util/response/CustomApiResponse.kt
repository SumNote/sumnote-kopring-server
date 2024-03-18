package com.capston.sumnote.util.response

class CustomApiResponse<T> private constructor(
    val status: Int,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> createSuccess(status: Int, data: T, message: String): CustomApiResponse<T> {
            return CustomApiResponse(status, data, message)
        }

        fun createFailWithoutData(status: Int, message: String): CustomApiResponse<Nothing> {
            return CustomApiResponse(status, null, message)
        }
    }
}