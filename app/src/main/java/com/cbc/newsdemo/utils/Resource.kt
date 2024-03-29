package com.cbc.newsdemo.utils

sealed class Resource<T>(
    //parameters
    val data: T?=null,
    val message: String?= null,

        ){

    //classes allowed to inherit
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T?= null): Resource<T>(data, message)
    class Loading<T>: Resource<T>()


}