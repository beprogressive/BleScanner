package com.example.myapplication.data.api.network

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.domain.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
): Repository {
}