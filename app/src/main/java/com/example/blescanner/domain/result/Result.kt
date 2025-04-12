package com.example.blescanner.domain.result

import com.example.blescanner.domain.error.AppException

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppException) : Result<Nothing>()
}
