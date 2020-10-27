package com.meliksahcakir.accountkeeper.utils

sealed class Result<out T : Any?> {
    data class Success<out T : Any?>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> {
                "data: ${this.data}"
            }
            is Error -> {
                "error: ${this.exception}"
            }
        }
    }
}