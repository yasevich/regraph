package com.github.yasevich.regraph.repository

import com.github.yasevich.regraph.model.AppError
import com.github.yasevich.regraph.model.AppStatus

class RepositoryResponse<out T> private constructor(
        val status: AppStatus,
        val error: AppError? = null,
        val result: T? = null
) {
    companion object {
        fun <T> success(result: T): RepositoryResponse<T> {
            return RepositoryResponse(status = AppStatus.SUCCESS, result = result)
        }

        fun <T> error(error: AppError): RepositoryResponse<T> {
            return RepositoryResponse(AppStatus.REFUSED, error)
        }
    }
}
