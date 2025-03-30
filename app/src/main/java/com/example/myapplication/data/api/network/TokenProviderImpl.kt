package com.example.myapplication.data.api.network

import com.example.myapplication.domain.TokenProvider
import javax.inject.Inject

class TokenProviderImpl @Inject constructor(): TokenProvider {
    override fun getToken(): String {
        TODO("Not yet implemented")
    }

    override fun saveToken(token: String) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}