package com.monsalud.fivedayforecaster.data.utils

sealed class DataError : Throwable() {
    data class Network(val errorType: Throwable) : DataError()
    data class Unknown(val errorType: Throwable) : DataError()
}