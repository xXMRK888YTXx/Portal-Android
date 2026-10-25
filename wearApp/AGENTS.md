# Wear OS Companion Module Guidelines (`wearApp`)

This module contains the Wear OS companion app for Portal, built with Jetpack Compose for Wear OS (
Material 3) and MVI architecture.

---

## 1. Product & Architecture Decisions

1. **Background Request Delivery on Wear OS**:
    - `SYSTEM_ALERT_WINDOW` is **not allowed** on Wear OS. Do not re-add it.
    - `USE_FULL_SCREEN_INTENT` is unreliable on inactive watches.
    - Incoming requests are delivered via **high-priority notification**.
    - Notifications contain quick-action buttons (**Allow** and **Deny**) to resolve requests
      directly from the notification tray without opening the full screen.
    - Tapping the notification body opens the dedicated `IncomingRequestActivity`.

2. **Decoupled Incoming Request Activity**:
    - Incoming requests on Wear OS run in their own dedicated `IncomingRequestActivity` (
      `noHistory="true"`, `excludeFromRecents="true"`, `showWhenLocked="true"`, isolated
      `taskAffinity`).
    - `MainActivity` is dedicated exclusively to app navigation (device list, device actions,
      settings, permission gate).

3. **First Decision Wins & Multi-node Sync**:
    - Coordinated via `IncomingUnlockDecisionCoordinatorImpl` on the phone.
    - Resolves the first decision (phone or watch), marks it complete, sends single unlock/cancel
      command to PC, and broadcasts final status via Wear Data Layer.
    - Cancels notifications and dismisses open screens across both devices simultaneously.

4. **Security & Secrets Isolation**:
    - Secrets, passwords, certificates, and IP/MAC addresses stay exclusively on the phone.
    - The watch receives only metadata (`clientId`, `name`, `transport`, `isWakeOnLanAvailable`).

---

## 2. Key Components & Architecture

- `wearApp/.../presentation/incomingRequest/IncomingRequestActivity.kt`: Separate Activity for
  incoming unlock requests with `AppScaffold(timeText = { TimeText() })`.
- `wearApp/.../presentation/incomingRequest/IncomingRequestScreen.kt`: Cancel/Unlock UI with
  localized `contentDescription`s (`allow`, `deny`) and `TextOverflow.Ellipsis`.
- `wearApp/.../data/broadcastReceiver/WearNotificationActionReceiver.kt`: Handles background
  Allow/Deny notification actions and dispatches decisions to phone.
- `wearApp/.../data/IncomingRequestPresenterImpl.kt`: Builds high-priority notifications with
  Allow/Deny action buttons and content intent pointing to `IncomingRequestActivity`.
- `wearApp/.../data/service/WearPortalListenerService.kt`: Receives profile sync, incoming unlock
  requests, and final statuses.
- `wearApp/.../presentation/mainActivity/MainActivity.kt`: Hosts app navigation via
  `SwipeDismissableNavHost` and `rememberSwipeDismissableNavController()`.
- `wearApp/.../presentation/deviceList/DeviceListScreen.kt`: Device list with
  `TransformingLazyColumn`, `SurfaceTransformation`, transport icons (`ic_wifi`, `ic_bluetooth`),
  and `TextOverflow.Ellipsis`.
- `wearApp/.../presentation/deviceActions/DeviceActionsScreen.kt`: Uses `ListHeader` (preventing
  button overlap), transport info in `secondaryLabel`, and a dimmed `CircularProgressIndicator`
  overlay during command execution.
- `wearApp/.../presentation/permissionGate/PermissionGateScreen.kt`: Redesigned permission screen
  with hero notification icon, clear title/description, and an edge-hugging `EdgeButton`.
- `wearApp/.../presentation/settings/SettingsScreen.kt`: Settings screen with action icons (
  `ic_notifications`, `ic_phone`), high-contrast status colors (`StatusConnectedColor`,
  `StatusDisconnectedColor`, `StatusCheckingColor`), and version footer.
- `wearApp/.../presentation/theme/Color.kt`: High-contrast status colors for dark Wear OS surfaces.

---

## 3. Wear OS Quality & UI Guidelines

- **Time Display**: `timeText = { TimeText() }` in `AppScaffold` across all activities.
- **Swipe Dismiss Navigation**: `SwipeDismissableNavHost` used for edge swipe-to-back gestures and
  smooth transitions.
- **Round Display Scaling**: `SurfaceTransformation(transformationSpec)` and
  `.transformedHeight(this, transformationSpec)` applied to cards, buttons, and headers in
  `TransformingLazyColumn`.
- **Rotary Input**: Linked automatically via `ScreenScaffold(scrollState = listState)`.
- **Accessibility**: Explicit localized `contentDescription`s on all icon buttons for TalkBack.
- **Text Overflow Protection**: `maxLines = 2` and `TextOverflow.Ellipsis` on device names across
  all screens.
- **Material 3 Confirmations**: Standard `Toast` replaced with `SuccessConfirmation` and
  `FailureConfirmation` dialogs from `androidx.wear.compose.material3`.

---

## 4. Release Build & R8 Protection

- **Resource Shrinking Protection**: `wearApp/src/main/res/raw/keep.xml` protects
  `@array/android_wear_capabilities` (`portal_phone_app`, `portal_watch_app`) from being stripped by
  `isShrinkResources = true`.
- **ProGuard / R8 Rules**: `wearApp/proguard-rules.pro` preserves `kotlinx.serialization`
  serializers, `@Serializable` models in `com.xxmrk888ytxx.portal.data.wear.**`, and Google Play
  Services Wearable components.

---

## 5. Architectural Constraints for Agents

- **MVI Architecture**: Maintain MVI architecture for all Wear screens: `*Screen.kt` accepts state
  and `onEvent: (ScreenEvent) -> Unit` and must not call ViewModels directly.
- **No Full-Screen Overlays**: Do not re-add `SYSTEM_ALERT_WINDOW` or full-screen intent on Wear OS.
- **Metadata Only**: Do not move network credentials or keys to the watch; watch profiles must
  remain metadata-only.
