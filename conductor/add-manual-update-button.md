# Plan: Add Manual Update Button

## Objective
Add a localized "Manual" button to the update popup (`UpdateBubble`) that opens the update URL in the external browser instead of downloading the update within the app.

## Key Files & Context
- `app/src/main/java/ir/hienob\noveo/ui/Localization.kt`: Needs a new localized string property for "Manual".
- `app/src/main/java/ir/hienob\noveo/ui/HomeUi.kt`: Needs to update the `UpdateBubble` composable to include the new button and handle the browser intent.

## Implementation Steps
1. **Localization**:
   - Add `val manualUpdate: String = "Manual"` to the default `NoveoStrings` data class in `Localization.kt`.
   - Add the equivalent translation for `manualUpdate` in the `translations` map for all supported languages (e.g., German, Russian, Chinese, Persian, Spanish, French, Arabic, Turkish).

2. **Update UI**:
   - In `HomeUi.kt`, locate the `UpdateBubble` composable.
   - Inject `LocalUriHandler.current` to handle external links.
   - Add a `TextButton` labeled "Manual" (using `strings.manualUpdate`) next to the "Dismiss" and "Update" buttons.
   - In the `onClick` handler of the "Manual" button, use `uriHandler.openUri(updateInfo.url)`.

## Verification & Testing
- Build the app and trigger a mock update or enable beta updates to display the update banner.
- Verify that the "Manual" button appears next to "Dismiss" and "Update".
- Verify that clicking "Manual" opens the system's default web browser to the provided `updateInfo.url`.
- Change the app language to verify that the "Manual" button text is properly translated.