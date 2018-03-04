package com.github.yasevich.regraph.repository

import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus

class RepositoryResponse<out T> private constructor(
        val status: AppStatus,
        private val _error: AppError? = null,
        private val _result: T? = null
) {

    val error: AppError
        get() = _error!!

    val result: T
        get() = _result!!

    companion object {
        fun <T> success(result: T): RepositoryResponse<T> {
            return RepositoryResponse(status = AppStatus.SUCCESS, _result = result)
        }

        fun <T> error(error: AppError): RepositoryResponse<T> {
            return RepositoryResponse(AppStatus.REFUSED, error)
        }
    }
}
