# Release Checklist & Sanity Checks for SANA PRO

This document lists the minimal steps and checks to prepare a release build for SANA PRO. Follow these steps before tagging a release.

## 1) Manifest sanity
- Verify `android:exported` attributes for Activities, Services, Receivers are set correctly (true for launcher Activity, exported=false for internal services unless needed).
- Confirm permissions in manifest are intentional; remove any unused sensitive permissions to minimize Play Store review risk.
- Ensure `uses-permission` entries for RECORD_AUDIO, FOREGROUND_SERVICE, RECEIVE_BOOT_COMPLETED, BIND_ACCESSIBILITY_SERVICE, POST_NOTIFICATIONS are present.
- Ensure FileProvider `authorities` uses `${applicationId}.fileprovider`.

## 2) Gradle sanity
- Compile and target SDK should be up-to-date (targetSdkVersion 33+ recommended).
- Ensure Java/Kotlin compile options use jvmTarget=17 and sourceCompatibility=VERSION_17.
- Remove debug-only dependencies and ProGuard rules interfering with reflection (e.g., okhttp/retrofit okhttp's internal models).

## 3) ProGuard / R8
- Add a `proguard-rules.pro` with rules for keeping model classes used via reflection and okhttp/okio.
- Keep data classes used by JSON libraries and any classes referenced by name from native code or manifest (e.g., services, receivers).

Example minimal rules (see proguard-rules.pro in repo):
-keep class com.sanaos.** { *; }
-keepclassmembers class com.sanaos.** { *; }
-keepattributes *Annotation*

## 4) Third-party API keys
- Do not hardcode API keys. Use remote configuration or request keys from user in Settings.
- Confirm SharedPrefs keys and UI fields (Gemini, ElevenLabs) work and are not logged.

## 5) Testing
- Run instrumentation tests on API 26, 29, 31, 33.
- Manual test list: STT flow, TTS flow, notification reader, accessibility features, boot receiver, reminders.

## 6) Play Store / Privacy
- Prepare Privacy Policy explaining Notification reading, Accessibility, Device admin usage, and data retention (interaction_history). Include opt-out instructions.
- Use Play Store declarations for sensitive permissions (e.g., RECORD_AUDIO, READ_CONTACTS) and justify usage.

## 7) Release build
- Build a signed release APK/AAB.
- Test the release-signed AAB on a physical device before publishing.

## 8) Changelogs
- Update CHANGELOG.md with user facing changes and bug fixes included in this release.

## 9) Rollout plan
- Use staged rollout (5–10%) and monitor crash logs and feedback.

---

Keep this checklist in the `docs/` folder and follow it for every release to reduce Play Store rejections and user friction.