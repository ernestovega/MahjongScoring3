# Mahjong Scoring 3

Kotlin Multiplatform + Compose Multiplatform port of [Mahjong Scoring 2](https://github.com/ernestovega/MahjongScoring2), with the same features, look and behavior, for Android and iOS.

## Structure

- `shared`: all the app. Domain (models, use cases), data (Room KMP, DataStore), Koin, ViewModels and Compose UI. Platform specifics live in `androidMain` / `iosMain` (`platform/Platform.*.kt`, `di/`).
- `androidApp`: Android entry point (`MainActivity`, `MahjongScoringApplication`).
- `iosApp`: Xcode project that embeds the `Shared` framework (`MainViewControllerKt.MainViewController()`).

## Compatibility with Mahjong Scoring 2

The Android app keeps MS2's `applicationId` (`com.mahjongscoring.activities`), so it is published as its update. It keeps the same Room database (`MahjongScoring2`, version 2) and DataStore file (`MahjongScoring2.preferences_pb`), so users keep their games and settings. The JSON export format is the same, so games can be moved between both apps and platforms.

## Build

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode (set your team in `iosApp/Configuration/Config.xcconfig`), or `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build`
- Tests: `./gradlew :shared:allTests`

## UI tests

Composables carry `Modifier.testTag(...)` with the same ids as MS2 views (for example `btHandActionsDialogHu`, `fabGameTable`), so MS2's UI tests can be ported to Compose UI tests that run on Android and iOS.

- **Screen sizes:** `appTest(screen = TestScreen.TABLET_LANDSCAPE)` runs a test on another screen (`PHONE`, `PHONE_LANDSCAPE`, `TABLET_LANDSCAPE`, `TABLET_PORTRAIT`). Android uses MS2's Robolectric qualifiers. iOS sizes its test window the same.
- **Rotation:** `rotate(screen) { ... }` emulates a rotation with an activity recreation (needs `appTest(restorable = true)`). Android resizes its windows. iOS moves the saved content to a new test window of the new size.
- **Edge-to-edge:** `sendInsets(systemBars, displayCutout, ime)` gives the app fake insets through `LocalAppInsetsOverride`, like MS2 `EdgeToEdgeTest`.
