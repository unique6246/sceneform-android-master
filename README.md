# Sceneform AR App

This Android project uses Sceneform to render 3D models (GLTF format) in Augmented Reality (AR) on supported Android devices. It uses Filament for physically based rendering and integrates ARCore for augmented reality functionality.

## Features

- Sceneform support with custom versioning
- ARCore integration
- GLTF model loading
- Kotlin & Coroutines
- Material Design Components
- Maven Central publish-ready
- Jetifier and AndroidX enabled

---

## Requirements

- **Android Studio**: Chipmunk or later (AGP 7.2.0+)
- **JDK**: 8 or newer
- **Android SDK**: Minimum API 24, Target API 34
- A device that supports **ARCore**

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/unique6246/sceneform-android-master.git
cd sceneform-android-master

git clone https://github.com/unique6246/Android_App.git
cd Android_App

```
### 2. Set Up SDK Location
- Ensure your local.properties or .properties file has:

sdk.dir=C:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
Update <YourUsername> to your Windows username.

### 3. Open in Android Studio
- Select Open an existing project
- Wait for Gradle sync to complete

Build & Run
Use the Gradle wrapper:
```bash
./gradlew assembleDebug
```
Or run directly from Android Studio using the green “Run” button.


Useful Gradle Commands
Print project properties:
```bash
./gradlew printProjectProperties
```

Clean build:
```bash
./gradlew clean
```
### 🛠️ Build Configuration
- compileSdk: 34

- minSdk: 24

- targetSdk: 34

- Kotlin: 1.6.21

- Jetifier: ✅

- AndroidX: ✅

- Proguard: enabled for release builds

- .filamat and .ktx files are excluded from compression

### 📦 Dependencies
## 🔤 Language + Concurrency
- Kotlin Standard Library

- Kotlinx Coroutines (core + Android)

## 📱 AndroidX & UI
- Core KTX

- AppCompat

- Fragment KTX

- Material Components

## 🎞️ Media
Glide (image loading)

## 🧱 Sceneform & Filament
- com.gorisse.thomas.sceneform:sceneform:1.23.0

- com.gorisse.sceneform:filament-utils:1.20.3

- com.gorisse.sceneform:core:1.17.1

## 📡 Networking
- Fuel (optional, based on your app logic)