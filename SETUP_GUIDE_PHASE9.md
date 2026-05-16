# SANA PRO 2.0 — PHASE 9 Setup Guide (AndroidIDE)

## 1) First Build Checklist
1. Open project in AndroidIDE v2.7.0-beta.
2. Sync Gradle and verify JDK 17 is used.
3. Confirm `gradle.properties` keeps memory at `-Xmx1536m`.
4. Build once with `assembleDebug`.

## 2) Runtime Permission Checklist
Grant on first run:
- Microphone
- Camera
- Location
- Phone + Call logs
- Contacts
- SMS
- Notifications (Android 13+)

If notification permission is denied on API 33+, foreground assistant startup will be deferred until granted.

## 3) Device Admin Enablement
1. Open app.
2. Trigger screen lock command once.
3. Android will open Device Admin enrollment screen.
4. Enable SANA Device Admin.

Used for: secure `lockNow()` flow in screen lock feature.

## 4) Accessibility Service Enablement
1. Open Settings > Accessibility.
2. Enable **SanaAccessibilityService**.
3. Confirm service can read current screen and scroll.

Used for: `ReadScreen`, `ScrollDown`, `ScrollUp`, `ScrollToTop`, `ScrollToBottom`.

## 5) Notification Listener Enablement
1. Open Settings > Notifications > Notification access.
2. Enable **NotificationReaderService**.

Used for: notification stream intake hooks.

## 6) Battery Monitoring Service
- Battery monitor service can announce threshold alerts at 100/50/20/10.
- Ensure app is not battery-optimized aggressively:
  - Settings > Apps > SANA > Battery > Unrestricted (recommended).

## 7) Required ADB Grants (for advanced toggles)
Run from host machine with device connected:

```bash
adb shell pm grant com.sanaos android.permission.WRITE_SECURE_SETTINGS
adb shell pm grant com.sanaos android.permission.DUMP
```

`WRITE_SECURE_SETTINGS` is needed for direct airplane-mode write path. Without it, app falls back to opening settings screens.

## 8) Foreground + Boot Behavior
- Foreground service starts only after critical permissions are granted.
- Boot auto-start requires:
  1. `RECEIVE_BOOT_COMPLETED` permission.
  2. `FOREGROUND_ENABLED` preference true.

## 9) API Key Setup
Inside Settings tab:
1. Enter Gemini API key.
2. Enter ElevenLabs API key.
3. Tap **Save Keys**.

Notes:
- Keys are Base64-encoded in SharedPreferences.
- ElevenLabs is primary TTS when network + key are available.
- Native Android TTS is automatic fallback.

## 10) Live Mode / Language / Call Mode
Settings options:
- Live Mode: keeps mic loop active after speaking.
- Language chips: Hinglish / English.
- Call mode:
  - `announce_only`
  - `receive_reject`

## 11) Crash-Prevention Validations
Before daily use, verify:
1. Emoji sanitizer active for ElevenLabs payload text.
2. Notification permission granted on Android 13+.
3. Offline network checks return user-friendly fallback before REST calls.

## 12) AndroidIDE-Specific Stability Tips
- Keep only this project open while building.
- Avoid parallel Gradle tasks on low-memory devices.
- If build cache corrupts, run clean + rebuild.
- If SpeechRecognizer gets busy repeatedly, restart app once to reset recognizer instance.

## 13) Quick Functional Smoke Test
1. Launch app.
2. Confirm Home/History/Settings navigation works.
3. Save API keys in Settings.
4. Start assistant and say:
   - “torch on”
   - “wifi on”
   - “set reminder 5 minute mein pani”
5. Disable internet and ask weather; confirm offline response.
6. Re-enable internet and test conversational query.

## 14) Production Readiness Checklist
- [ ] All runtime permissions granted
- [ ] Device Admin enabled
- [ ] Accessibility service enabled
- [ ] Notification listener enabled
- [ ] API keys saved
- [ ] Boot/start behavior verified
- [ ] Offline fallback behavior verified
