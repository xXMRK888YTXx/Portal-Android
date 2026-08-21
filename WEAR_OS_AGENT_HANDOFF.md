# Wear OS unlock feature handoff

This repository contains the Wear OS companion app in `wearApp` and the phone-side backend in `app`
and core modules.
The user asked **not to run compilation/tests** (`./gradlew build`, etc.) unless explicitly
requested.

---

## Current Product & Architecture Decisions

1. **Background Request Delivery on Wear OS**:
    - `SYSTEM_ALERT_WINDOW` is not allowed on Wear OS.
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

## Key Components & Architecture

### Phone Side (`app/`, `core/`)

- `app/.../data/UnlockRequestHandlerImpl.kt`: Creates `decisionId` and forwards unlock requests to
  Wear OS.
- `app/.../data/IncomingUnlockDecisionCoordinatorImpl.kt`:
    - Central decision deduplication.
    - Bounded LRU history (`MAX_COMPLETED_HISTORY = 100`) and 5-minute TTL expiration for pending
      requests to prevent memory leaks.
    - Exposes `finalStatus` as `SharedFlow(replay = 0, extraBufferCapacity = 64)` to prevent stale
      replay to new subscribers.
- `app/.../data/UnlockRequestManagerImpl.kt`: Manages phone notifications and cancels them on final
  status.
- `app/.../view/unlockScreenActivity/UnlockScreenActivity.kt` & `UnlockScreenViewModel.kt`:
    - Dismisses phone unlock screen on incoming watch decision.
    - Implements `onNewIntent` to handle subsequent requests safely, canceling previous status
      observation jobs.
- `app/.../data/wear/*`: Wear Data Layer protocol, sync manager, phone gateway, and node validation
  using non-blocking `kotlinx.coroutines.tasks.await()`.
- `core/database/PortalDataBase.kt`:
    - Room database (version 3) with `MIGRATION_1_2` and `MIGRATION_2_3`.
    - `ShortcutEntry` indexed on `clientId` foreign key to prevent SQLite full table scans.
- `app/.../providedContract/settingsScreen/ProvideSettingsStateImpl.kt`: Exposes version as
  `1.0.0-debug (1)` in debug and `1.0.0 (1)` in release.

### Watch Side (`wearApp/`)

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
  with hero notification icon, clear title/description, and direct CTA button.
- `wearApp/.../presentation/settings/SettingsScreen.kt`: Settings screen with action icons (
  `ic_notifications`, `ic_phone`), high-contrast status colors (`StatusConnectedColor`,
  `StatusDisconnectedColor`, `StatusCheckingColor`), and version footer.
- `wearApp/.../presentation/theme/Color.kt`: High-contrast status colors for dark Wear OS surfaces.

---

## Wear OS Quality & UI Guidelines Implemented

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

## Release Build & R8 Protection

- **Resource Shrinking Protection**: `app/src/main/res/raw/keep.xml` and
  `wearApp/src/main/res/raw/keep.xml` protect `@array/android_wear_capabilities` (
  `portal_phone_app`, `portal_watch_app`) from being stripped by `isShrinkResources = true`.
- **ProGuard / R8 Rules**: `app/proguard-rules.pro` and `wearApp/proguard-rules.pro` preserve
  `kotlinx.serialization` serializers, `@Serializable` models in
  `com.xxmrk888ytxx.portal.data.wear.**`, and Google Play Services Wearable components.

---

## Important Guidelines for Future Agents

- Do not re-add `SYSTEM_ALERT_WINDOW` or full-screen intent on Wear OS.
- Do not move network credentials or keys to the watch; watch profiles must remain metadata-only.
- Maintain MVI architecture for all Wear screens: `*Screen.kt` accepts state and
  `onEvent: (ScreenEvent) -> Unit` and does not call ViewModels directly.
