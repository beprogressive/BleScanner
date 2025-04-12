package com.example.blescanner.domain.usecases

import com.example.blescanner.domain.repositories.BleRepo
import javax.inject.Inject

class StopScan @Inject constructor(
    private val bleRepo: BleRepo
) {
    operator fun invoke() = bleRepo.stopScan()
}