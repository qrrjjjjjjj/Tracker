# Tracker

Android application untuk melacak notifikasi dari aplikasi yang dipilih pengguna.

## Fitur

- 🔔 Melacak notifikasi real-time dari aplikasi yang dipilih
- 📱 Interface modern dengan Material Design 3
- 🌙 Dark mode dengan efek glassmorphism
- 💾 Penyimpanan persisten menggunakan Room Database
- ⚡ State management reaktif dengan Kotlin Flow
- 🎯 Filter aplikasi dengan pencarian
- 📊 Riwayat notifikasi lengkap

## Teknologi

- **Bahasa**: Kotlin
- **Build**: Gradle (KTS)
- **UI**: Jetpack Compose + Material3
- **Arsitektur**: MVVM + Repository Pattern
- **Database**: Room
- **Preferences**: DataStore
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

## Setup

1. Clone repository
2. Buka di Android Studio
3. Generate launcher icons (opsional):
   - Right-click `res` > New > Image Asset
4. Download font Roboto (opsional - sudah menggunakan system font):
   - https://fonts.google.com/specimen/Roboto
   - Tempatkan di `app/src/main/res/font/`
5. Sync Gradle
6. Build & Run

## Permissions

Aplikasi membutuhkan akses Notification Listener untuk membaca notifikasi. Pengguna akan diminta untuk mengaktifkannya melalui System Settings.

## Build

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

## CI/CD

Project menggunakan GitHub Actions untuk automated build. Setiap push akan:
- Build APK release
- Upload sebagai artifact
- Set version code dari run number
- Set version name dari tanggal

## Struktur Proyek

```
app/src/main/java/com/lyciv/tracker/
├── MainActivity.kt
├── core/
│   └── TrackerNotificationListener.kt
├── data/
│   ├── NotificationsRepository.kt
│   ├── db/
│   │   ├── NotificationEntity.kt
│   │   ├── NotificationDao.kt
│   │   └── AppDatabase.kt
│   └── prefs/
│       ├── UserPrefs.kt
│       └── PrefsRepository.kt
├── model/
│   ├── TrackedApp.kt
│   └── NotificationItem.kt
├── ui/
│   ├── MainScreen.kt
│   ├── components/
│   │   ├── GlassSurface.kt
│   │   ├── EmptyState.kt
│   │   ├── TopBar.kt
│   │   └── SearchField.kt
│   ├── log/
│   │   └── LogScreen.kt
│   ├── apps/
│   │   └── AppsScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
├── util/
│   ├── AppsQuery.kt
│   └── TimeFormat.kt
└── viewmodel/
    └── MainViewModel.kt
```

## License

MIT License
