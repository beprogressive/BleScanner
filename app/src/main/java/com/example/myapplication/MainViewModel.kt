package com.example.myapplication

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(repository: Repository): ViewModel() {

    init {
        Timber.d("MainViewModel injected")
        Timber.d("Repository injected: $repository")
    }

    fun getTodo() {
        // repository.getTodo()
    }
}