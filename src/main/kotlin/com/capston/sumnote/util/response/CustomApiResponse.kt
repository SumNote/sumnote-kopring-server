package com.capston.sumnote.util.response

class CustomApiResponse<T>(
    val status: Int,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> createSuccess(status: Int, data: T, message: String): CustomApiResponse<T> {
            return CustomApiResponse(status, data, message)
        }

        fun <T> createSuccessWithoutData(status: Int, message: String): CustomApiResponse<T> {
            return CustomApiResponse(status, null, message)
        }

        fun createFailWithoutData(status: Int, message: String): CustomApiResponse<Nothing> {
            return CustomApiResponse(status, null, message)
        }
    }
}