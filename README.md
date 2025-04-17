# BLE Scanner

An Android application for scanning and discovering Bluetooth Low Energy (BLE) devices. The app demonstrates modern Android development practices, clean architecture principles and testing.

## Features

- Scan for nearby BLE devices
- Display device name, MAC address, and signal strength (RSSI)
- Auto-stop scanning after timeout to preserve battery
- Auto-request required permissions
- Support for both light and dark themes

## Architecture

This project uses Clean Architecture with a clear separation of concerns:

```
app/
├── data/         # Data layer (repositories implementation, exceptions)
├── domain/       # Domain layer (models, repositories interfaces, use cases)
└── ui/           # UI layer (activities, viewmodels, composables)
```

## Technology Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture Components**:
  - ViewModel
  - StateFlow for reactive UI
  - Hilt for dependency injection
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**:
  - JUnit for unit tests
  - Mockk for mocking
  - Turbine for testing Flows
  - StandardTestDispatcher for controlled test environment
- **Logging**: Timber


## Requirements

- Android 7.0 (API level 24) or higher
- Bluetooth Low Energy capable device
- Location and Bluetooth permissions
