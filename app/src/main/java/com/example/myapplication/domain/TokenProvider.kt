package com.example.myapplication.domain

interface TokenProvider {
    fun getToken(): String
    fun saveToken(token: String)
    fun clear()
}