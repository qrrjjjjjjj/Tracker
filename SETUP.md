# Setup Guide - Tracker

## Prasyarat

- Android Studio Ladybug atau lebih baru (2024.2.1+)
- JDK 21 (Temurin/OpenJDK)
- Android SDK 35
- Gradle 8.11+

## Langkah Setup

### 1. Clone Repository

```bash
git clone <repository-url>
cd Tracker
```

### 2. Install Dependencies

Buka project di Android Studio, maka otomatis akan:
- Download Gradle wrapper
- Sync dependencies
- Setup SDK

Atau via command line:

```bash
chmod +x gradlew
./gradlew --version
```

### 3. Launcher Icon (Opsional)

Project sudah menggunakan default Android launcher icon. Jika ingin custom:

1. Buka Android Studio
2. Right-click `app/src/main/res`
3. New > Image Asset
4. Pilih icon source (image/clipart)
5. Generate untuk semua densities

### 4. Build Project

**Debug Build:**
```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release Build:**
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 5. Install ke Device

**Via ADB:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Via Android Studio:**
1. Connect device atau start emulator
2. Click Run (Shift+F10)

## Setup GitHub Actions

File `.github/workflows/android.yml` sudah disediakan. Untuk mengaktifkan:

1. Push ke repository GitHub
2. Actions akan otomatis berjalan
3. APK release dapat didownload dari Artifacts

## Struktur File Penting

```
Tracker/
├── app/
│   ├── build.gradle.kts          # Konfigurasi app module
│   ├── proguard-rules.pro        # Rules untuk minify release
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/lyciv/tracker/
│           └── res/
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
└── gradle/wrapper/               # Gradle wrapper files
```

## Troubleshooting

### Gradle Sync Gagal

```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### Build Gagal - Out of Memory

Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### Cannot Resolve Symbol

1. File > Invalidate Caches
2. Restart Android Studio
3. Rebuild Project

### ADB Device Not Found

```bash
adb kill-server
adb start-server
adb devices
```

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Release Signing (Opsional)

Untuk signed APK, buat file `keystore.properties`:

```properties
storePassword=<password>
keyPassword=<password>
keyAlias=<alias>
storeFile=<path-to-keystore>
```

Tambahkan di `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile = file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        // ...
    }
}
```

## Minimum Requirements

- **Device**: Android 8.0 (API 26) atau lebih tinggi
- **RAM**: 2GB minimum
- **Storage**: 50MB untuk aplikasi

## Permissions

Aplikasi memerlukan:
- Notification Listener Service access (manual activation via Settings)
- Tidak ada runtime permissions

## Next Steps

1. Jalankan aplikasi
2. Tekan "Open Settings" untuk enable Notification Access
3. Pilih aplikasi yang ingin dilacak di tab Apps
4. Notifikasi akan muncul di tab Log

## Support

Untuk issues atau pertanyaan, buat issue di GitHub repository.
