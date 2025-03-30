package com.example.myapplication.data.api

import retrofit2.http.GET

interface ApiService {
    @GET("todos/1")
    suspend fun getTodo(): TodoResponse
}

data class TodoResponse(
    val id: Int,
    val title: String,
    val completed: Boolean
)