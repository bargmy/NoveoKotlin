# Project Map

## Build And Release

- Root Gradle config: `build.gradle.kts`
- Module list: `settings.gradle.kts`
- App module config: `app/build.gradle.kts`
- CI release build: `.github/workflows/build.yml`
- Utility scripts: `scripts/`

## Main App Surface

- Entry activity: `app/src/main/java/ir/hienob/noveo/MainActivity.kt`
- Application class: `app/src/main/java/ir/hienob/noveo/NoveoApplication.kt`
- App state and orchestration: `app/src/main/java/ir/hienob/noveo/app/AppViewModel.kt`
- Main navigation and screen definitions: `app/src/main/java/ir/hienob/noveo/ui/AppScreens.kt`

## High-Churn UI Files

- Large home and chat UI surface:
  - `app/src/main/java/ir/hienob/noveo/ui/HomeUi.kt`
- Chat composer and recording UI:
  - `app/src/main/java/ir/hienob/noveo/ui/ChatInput.kt`
- Context menu behavior:
  - `app/src/main/java/ir/hienob/noveo/ui/MessageContextMenu.kt`
- Strings and localization:
  - `app/src/main/java/ir/hienob/noveo/ui/Localization.kt`
- Theme constants:
  - `app/src/main/java/ir/hienob/noveo/ui/ThemeConstants.kt`

## Data And Networking

- API client: `app/src/main/java/ir/hienob/noveo/data/NoveoApi.kt`
- WebSocket integration: `app/src/main/java/ir/hienob/noveo/data/ChatSocket.kt`
- App models: `app/src/main/java/ir/hienob/noveo/data/Models.kt`
- Sync parsers: `app/src/main/java/ir/hienob/noveo/data/SyncParsers.kt`
- Session storage: `app/src/main/java/ir/hienob/noveo/data/SessionStore.kt`
- Core endpoints: `core/network/src/main/kotlin/ir/hienob/noveo/core/network/NoveoEndpoints.kt`

## Background And Notifications

- Notification service: `app/src/main/java/ir/hienob/noveo/background/NoveoNotificationService.kt`
- Notification actions: `app/src/main/java/ir/hienob/noveo/background/NotificationActionReceiver.kt`
- Notification channels: `core/notifications/src/main/kotlin/ir/hienob/noveo/core/notifications/NotificationChannels.kt`

## Voice And Storage Support

- Voice contracts: `core/voice/src/main/kotlin/ir/hienob/noveo/core/voice/VoiceParityContract.kt`
- Datastore keys: `core/datastore/src/main/kotlin/ir/hienob/noveo/core/datastore/WebParityStorageKeys.kt`

## Practical Entry Points

- UI bug in chat or home: start with `HomeUi.kt`, then inspect `AppViewModel.kt`.
- Composer, attachment, or recording bug: start with `ChatInput.kt`, then inspect `AudioRecorder.kt` and `AppViewModel.kt`.
- Login, sync, or session bug: start with `AppViewModel.kt`, then inspect `NoveoApi.kt`, `ChatSocket.kt`, and `SessionStore.kt`.
- Update flow bug: inspect `AppViewModel.kt`, `NoveoApi.kt`, `update.json`, and `MainActivity.kt`.
- Notification bug: inspect the background package and `core/notifications`.
