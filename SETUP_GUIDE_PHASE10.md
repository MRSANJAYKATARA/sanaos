# SANA PRO 2.0 — PHASE 10 Hardening & Release Guide (AndroidIDE)

## 1) Objective
PHASE 10 focuses on converting the generated skeleton into a safer and more predictable build by validating high-risk runtime paths and release toggles.

## 2) Pre-flight Repository Checks
1. Ensure all app sources compile against JDK 17.
2. Confirm `applicationId` is `com.sanaos` across app manifest and Gradle config.
3. Confirm min/target SDK values are aligned with current AndroidIDE plugin configuration.

## 3) Security Hardening Pass
1. Verify no raw API keys are hardcoded in Kotlin/XML/assets.
2. Keep credential persistence only in `SharedPreferences` through existing manager APIs.
3. Confirm exported components in `AndroidManifest.xml` are explicitly declared and justified.
4. Ensure no debug logging prints API keys, auth headers, or full payload secrets.

## 4) Permission Degradation Matrix
For each permission-dependent feature, verify degraded fallback behavior:
- Mic denied → assistant does not crash; shows user-facing explanation.
- Notification denied on API 33+ → foreground path defers with clear guidance.
- Location denied → weather/location features return fallback text.
- Phone/SMS denied → call/sms features return safe error message only.

## 5) Service Lifecycle Validation
1. Start and stop `SanaForegroundService` repeatedly (10x) to verify no leaked recognizer/audio focus state.
2. Simulate process death and relaunch to verify preference restoration.
3. Reboot device/emulator and validate boot receiver conditional startup logic.

## 6) Accessibility + Global Action Reliability
1. With accessibility enabled, run scroll/read commands on at least two third-party apps.
2. Validate node traversal does not throw NPE on empty roots.
3. Confirm global actions fail gracefully if service is disabled mid-session.

## 7) Network Resilience Checks
1. Disable internet and run cloud-dependent query.
2. Verify app returns deterministic offline fallback before API call.
3. Re-enable internet and verify cloud pathway recovers without app restart.

## 8) Audio/TTS Stability
1. Test ElevenLabs TTS with valid key.
2. Force network loss and verify native Android TTS fallback.
3. Interrupt speaking via stop action and verify audio focus release.

## 9) Database/History Consistency
1. Insert repeated chat items and verify no SQLite crash.
2. Validate history list rendering after cold restart.
3. Confirm helper closes resources on shutdown paths.

## 10) Release Build Readiness
1. Build `assembleRelease` once local environment has a complete Gradle wrapper.
2. Confirm ProGuard/R8 rules preserve critical service classes.
3. Verify versionCode/versionName increment policy before distribution.

## 11) Manual Smoke Script (Phase 10)
Run this command sequence manually in-app:
1. “wifi on”
2. “bluetooth off”
3. “read screen”
4. “scroll down”
5. “set reminder 2 minute take break”
6. Disable network → ask weather question
7. Re-enable network → ask general conversational query

Expected result: no crash, clear spoken feedback, safe fallback on unavailable capabilities.

## 12) Exit Criteria
- [ ] No critical crash during 30-minute mixed interaction session.
- [ ] All denied-permission paths produce user-safe messages.
- [ ] Offline fallback is deterministic and consistent.
- [ ] Key services restart cleanly.
- [ ] Release build checklist is complete.
