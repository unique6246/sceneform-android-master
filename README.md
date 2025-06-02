# Sceneform AR App (GLTF Sample)

An Android application that renders 3D GLTF models using Sceneform and Filament in Augmented Reality (AR).  
⚠️ This project **requires a companion Android app** to be installed on the device **before** this app can run correctly. It also depends on a JDK installed via **Homebrew**.

---

### 📦 Project Overview

- Built with **Kotlin**, **Coroutines**, and **AndroidX**
- Uses **Sceneform**, **Filament**, and **ARCore**
- Loads and renders **GLTF** 3D assets
- Configured for **Maven Central Publishing**
- Requires **Homebrew-installed JDK**
- Not a standalone app — relies on another **companion app** installed beforehand
---
### 🧱 Prerequisites

### ✅ 1. Install the Companion App (REQUIRED)

This app depends on a separate companion app (e.g. background service or model provider).  
You **must** install this app on the device before launching this application.

> If the companion app is not present, this app will fail to start or function properly.

```bash
git clone https://github.com/unique6246/sceneform-android-master.git
cd sceneform-android-master

git clone https://github.com/unique6246/Android_App.git
cd Android_App
```
---
### 🍺 2. Install Homebrew (macOS/Linux)

If not already installed:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
---
### 3. Set Up SDK Location
- Ensure your local.properties or .properties file has:
- sdk.dir=C:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
- Update <YourUsername> to your Windows username.

---
### 4. Open in Android Studio
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
---
### 5. 🛠️ Build Configuration
- compileSdk: 34

- minSdk: 24

- targetSdk: 34

- Kotlin: 1.6.21

- Jetifier: ✅

- AndroidX: ✅

- Proguard: enabled for release builds

- .filamat and .ktx files are excluded from compression

---
### 6. 📦 Dependencies
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