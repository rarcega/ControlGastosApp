# Fix Missing Main Dispatcher and SLF4J Warning

The desktop application is crashing because the `Main` coroutine dispatcher is not found. In Compose Desktop, this dispatcher is provided by the `kotlinx-coroutines-swing` library. Additionally, there is a warning about missing SLF4J providers which can be resolved by adding a simple logger implementation.

## Proposed Changes

### Dependencies
#### [MODIFY] [composeApp/build.gradle.kts](file:///D:/Proyectos Android/ControlGastosApp/composeApp/build.gradle.kts)
- Add `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1` to `commonMain`.
- Add `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1` to `androidMain`.
- Add `org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.1` to `desktopMain`.
- Add `org.slf4j:slf4j-simple:2.0.16` to `desktopMain`.

## Verification Plan

### Automated Tests
- Run `.\gradlew :composeApp:run` to verify the app starts without the "Main dispatcher is missing" error.

### Manual Verification
- Verify the app UI appears and Dashboard data loading works correctly.
