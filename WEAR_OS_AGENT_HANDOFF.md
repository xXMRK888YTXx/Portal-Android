# Wear OS unlock feature handoff

This repository currently contains an in-progress Wear OS companion app in `wearApp`.
The user asked not to run compilation/tests unless explicitly requested.

## Current user intent

The Wear OS app lets a user unlock a PC through the paired phone and receive incoming PC unlock
requests on the watch.

Important recent product decision:

- `SYSTEM_ALERT_WINDOW` is not allowed for Wear OS.
- `USE_FULL_SCREEN_INTENT` also did not work reliably for inactive watches.
- Therefore incoming requests on inactive/background watches should be handled by a high-priority
  notification only.
- Tapping the notification opens the same incoming request screen.
- The incoming request screen must use two buttons, not a slider:
    - `✕` cancels.
    - `✓` confirms unlock.

## Key behavior

### Phone request → watch

When a PC sends an unlock request to the phone:

1. Phone registers/uses a `decisionId` via `IncomingUnlockDecisionCoordinator`.
2. Phone shows its normal UI/notification.
3. If profile setting allows forwarding to Wear, phone sends an incoming request message to the
   watch.
4. Watch stores the pending request and posts a notification.
5. Notification opens `wearApp` incoming request screen.

### First decision wins

`app/src/main/java/com/xxmrk888ytxx/portal/data/IncomingUnlockDecisionCoordinatorImpl.kt`
centralizes decisions:

- first decision sends exactly one unlock/cancel message to the PC;
- later decisions for the same `decisionId` are treated as already completed;
- final status is sent to watches through Wear Data Layer;
- phone-side `finalStatus` also carries `clientId` so local phone notifications can be canceled.

### Closing stale UI/notifications

Expected behavior after the latest fix:

- If phone confirms/cancels first:
    - watch receives `FINAL_STATUS_PATH`;
    - watch cancels its notification in `IncomingRequestPresenter.cancel(decisionId)`;
    - watch screen marks the request completed.
- If watch confirms/cancels first:
    - phone coordinator resolves the request;
    - phone unlock screen dismisses via `decisionCoordinator.finalStatus`;
    - phone unlock notification is canceled by `UnlockRequestManagerImpl`.

## Important files

Phone side:

- `app/src/main/java/com/xxmrk888ytxx/portal/data/UnlockRequestHandlerImpl.kt`
    - creates/registers `decisionId`;
    - forwards requests to Wear.
- `app/src/main/java/com/xxmrk888ytxx/portal/data/IncomingUnlockDecisionCoordinatorImpl.kt`
    - deduplicates decisions;
    - sends final status to Wear;
    - emits phone-side final status.
- `app/src/main/java/com/xxmrk888ytxx/portal/domain/IncomingUnlockDecisionCoordinator.kt`
    - coordinator contract and `IncomingUnlockFinalStatus`.
- `app/src/main/java/com/xxmrk888ytxx/portal/data/UnlockRequestManagerImpl.kt`
    - shows phone activity/notification;
    - now cancels phone notification when final status contains `clientId`.
- `app/src/main/java/com/xxmrk888ytxx/portal/view/unlockScreenActivity/UnlockScreenViewModel.kt`
    - dismisses phone unlock screen when final status with matching `decisionId` appears.
- `app/src/main/java/com/xxmrk888ytxx/portal/data/wear/*`
    - Wear Data Layer protocol, sync manager, phone gateway and command handler.

Watch side:

- `wearApp/src/main/AndroidManifest.xml`
    - uses `POST_NOTIFICATIONS`;
    - does not use `SYSTEM_ALERT_WINDOW` or `USE_FULL_SCREEN_INTENT`.
- `wearApp/src/main/java/com/xxmrk888ytxx/portal/data/IncomingRequestPresenterImpl.kt`
    - posts/cancels watch incoming request notifications;
    - no full-screen intent.
- `wearApp/src/main/java/com/xxmrk888ytxx/portal/data/service/WearPortalListenerService.kt`
    - receives profile sync, incoming requests and final status.
-
`wearApp/src/main/java/com/xxmrk888ytxx/portal/presentation/incomingRequest/IncomingRequestScreen.kt`
    - incoming request screen with two buttons (`✕` / `✓`).
-
`wearApp/src/main/java/com/xxmrk888ytxx/portal/presentation/permissionGate/PermissionGateScreen.kt`
    - blocks app until notification permission is available.
- `wearApp/src/main/java/com/xxmrk888ytxx/portal/presentation/settings/SettingsScreen.kt`
    - shows notification permission status, phone connection status and app version.

## Naming / architecture notes

- Watch-side synced PCs are named `Device`, not `WearProfile`.
- `wearApp` is intended to stay monomodule, MVI-ish, Dagger DI.
- Wear presentation screens should follow the main app MVI style:
    - `*Screen.kt` accepts state/data and `onEvent: (ScreenEvent) -> Unit`;
    - screen Composables must not call ViewModel/navigator methods directly;
    - each screen ViewModel exposes `handleEvent(event)` and emits side effects for
      navigation/toasts.
- Watch-side persistence uses `PreferencesStorage`/DataStore, not native `SharedPreferences`.
- Do not reintroduce `@Synchronized`; use coroutines primitives such as `Mutex`.

## Static checks already used

These were run without compiling:

```powershell
rg "FullScreen|full_screen|USE_FULL_SCREEN|canUseFullScreenIntent|openFullScreenIntentSettings|OpenFullScreenIntentSettings|DecisionSlider|slide_|showRequestsOnLockedScreen|WearSettingsRepository|WearSettingsRepositoryImpl|SYSTEM_ALERT_WINDOW|setFullScreenIntent" wearApp/src/main -n
git diff --check
```

`git diff --check` only reported CRLF warnings from Git, not whitespace errors.

## Do not assume

- Do not assume compilation is green; user repeatedly asked not to compile unless they say so.
- Do not assume full-screen presentation on inactive Wear OS devices is available.
- Do not move secrets/network details to the watch. Watch profile sync should remain metadata-only.
