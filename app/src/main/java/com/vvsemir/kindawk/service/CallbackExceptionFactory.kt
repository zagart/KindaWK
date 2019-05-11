package com.vvsemir.kindawk.service

interface CallbackExceptionFactory {
    companion object {
        const val THROWABLE_TYPE_ERROR = 1
        const val THROWABLE_TYPE_EXCEPTION_DB = 2
        const val THROWABLE_TYPE_EXCEPTION_SERVICE = 3
        const val THROWABLE_TYPE_EXCEPTION_HTTP = 4
        const val THROWABLE_TYPE_EXCEPTION_NETWORK = 5
        const val THROWABLE_EXCEPTION_UNKNOWN_MSG = "Uknown exception type"
        fun createException(type : Int, message: String) : Throwable =
                when (type) {
                    THROWABLE_TYPE_ERROR -> Error(message)
                    THROWABLE_TYPE_EXCEPTION_DB -> DbException(message)
                    THROWABLE_TYPE_EXCEPTION_SERVICE -> ServiceException(message)
                    THROWABLE_TYPE_EXCEPTION_HTTP -> HttpException(message)
                    THROWABLE_TYPE_EXCEPTION_NETWORK -> NetworkException(message)
                    else -> throw Exception(THROWABLE_EXCEPTION_UNKNOWN_MSG)
                }
        class DbException(message: String) : Exception(message)
        class HttpException(message: String) : Exception(message)
        class NetworkException(message: String) : Exception(message)
        class ServiceException(message: String) : Exception(message)
    }
}