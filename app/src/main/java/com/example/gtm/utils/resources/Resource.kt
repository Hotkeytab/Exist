package com.example.gtm.utils.resources

data class Resource<out T>(val status: Status, val data: T?, val message: String?,val responseCode:Int?) {


    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {

        fun <T> success(data: T,responseCode: Int?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, responseCode)
        }

        fun <T> error(message: String, data: T? = null,responseCode: Int?): Resource<T> {
            return Resource(Status.ERROR, data, message,responseCode)
        }


        fun <T> loading(data: T? = null,responseCode: Int?): Resource<T> {
            return Resource(Status.LOADING, data, null,responseCode)
        }
    }
}