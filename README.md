# Namma-Raste-Reporter

![License](https://img.shields.io/badge/license-MIT-blue.svg)

Namma-Raste-Reporter is an Android citizen service app built with Jetpack Compose. It enables users to log in, report civic issues with GPS-backed location data and camera evidence, track submitted reports, and manage profile information.

## Features

- User authentication: login, register, reset password
- Multi-step issue reporting form with location detection
- Capture issue evidence with CameraX
- Track submitted reports and view report details
- Notifications screen for app updates
- Profile editing and local preference management
- Offline-capable local storage with Room
- Dependency injection with Hilt
- Modern Compose UI with dark theme support

## Tech Stack

- Kotlin
- Android SDK 35
- Jetpack Compose
- Hilt
- Room
- CameraX
- Play Services Location GPS
- DataStore Preferences


## Project Details

- Package / namespace: `com.nagarseva.app`
- Minimum SDK: 26
- Target SDK: 35
- Compile SDK: 35

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd Namma-Raste-Reporter
   ```
2. Open the project in Android Studio.
3. Sync Gradle and let Android Studio download dependencies.
4. Run the `app` module on an emulator or connected device.

## Build

```bash
./gradlew clean build
./gradlew assembleDebug
```

## Install on Device

```bash
./gradlew installDebug
```

## Testing

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Project Structure

- `app/src/main/java` — application source code
- `app/src/main/res` — UI resources and assets
- `app/build.gradle.kts` — module build configuration
- `build.gradle.kts` — top-level Gradle settings
- `gradle/libs.versions.toml` — dependency versions

## Screenshot
<img width="300" height="450" alt="home page" src="https://github.com/user-attachments/assets/277337f0-3bf7-4a00-8f05-f3ed96ae428f" /> 
<img width="300" height="450" alt="status tracker" src="https://github.com/user-attachments/assets/170ce34f-5d65-4b4a-88c9-115b4f40958f" />
<img width="1023" height="1537" alt="my reports" src="https://github.com/user-attachments/assets/99be333e-41cf-4fd9-ba0e-2d37cc94c775" />


## Contributing

Contributions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Open a pull request.
