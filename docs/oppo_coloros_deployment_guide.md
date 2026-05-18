# OPPO / ColorOS Deployment Guide (Sana Pro 2.0)

_This guide is written in Hindi/Hinglish for Sanjay Boss and QA engineers. Follow step-by-step to ensure SANA works reliably on Oppo/ColorOS devices (and similar aggressive OEM skins)._ 

## Overview
Oppo/ColorOS aggressively restricts background execution, auto-start, and some special permissions. To make SANA reliable, follow these steps on a target device and when guiding users during first-run onboarding.

---

## 1) Oppo Battery / App Launch settings (App must be allowed to auto-launch and run in background)
1. Open Settings → Battery → App Battery Management (or Battery Optimization)
2. Find the SANA app ("SANA PRO" or the app name shown) and tap it.
3. Choose "Manage automatically" or select "Manual"/"Battery management" then set to "Allow background activity" / "No restrictions".
4. If there is an "App launch" screen:
   - Set Auto-launch = ON
   - Allow Secondary launch = ON
   - Allow Run in background = ON
5. Reboot the device to confirm settings persist.

Why: ColorOS kills background services aggressively. Allowing these prevents the foreground service from being stopped by the OEM.

---

## 2) Oppo Auto-start (Auto-launch in newer ColorOS versions)
1. Settings → Security or Privacy → App Management → Auto-launch (or App auto-start)
2. Enable Auto-start for SANA.

Why: Without this, the app may not receive BOOT_COMPLETED or be allowed to start for scheduled reminders.

---

## 3) Accessibility (required for Accessibility features & screen reading)
1. Settings → Additional Settings → Accessibility → Installed apps / Downloaded apps (path varies)
2. Find "SANA" and enable the Accessibility Service.
3. Confirm any dialogs. Allow all requested capabilities on the prompt.

Notes: If Accessibility permission is not given, features like Read Screen, Scroll, and Accessibility actions will fail. Prompt the user with a guided flow explaining why SANA needs this permission.

---

## 4) Notification Listener (for NotificationReaderService)
1. Settings → Notification & Control Center → Notification Management → Notification Access (or Settings → Apps & notifications → Special app access → Notification access)
2. Enable SANA's notification access.
3. Confirm any security prompts.

Behavior: When enabled, SANA will broadcast notification content locally and can speak incoming important messages (live mode). Respect privacy and only read when user enables live mode.

---

## 5) Device Admin (for locking and secure actions)
1. Settings → Security → Device Administrators (or Device admin apps)
2. Activate SANA Device Admin when prompted (this enables lock screen and device admin features).
3. To deactivate, user must explicitly open device admin and revoke; inform users of this flow in UI.

Caution: Device admin gives sensitive rights; explain clearly in-app why it is needed (e.g., Lock Screen voice command).

---

## 6) WRITE_SETTINGS and Special Permissions
WRITE_SETTINGS is a special permission (modify system settings) and must be manually granted:
1. Settings → Apps → Special access → Modify system settings
2. Find SANA and enable "Allow modify system settings".

MANAGE_EXTERNAL_STORAGE (Scoped storage) for file manager features on API 30+:
1. Settings → Apps → Special access → All files access
2. Give SANA permission if FileManager features require wide storage.

Also ensure Camera, Microphone, Contacts, Call, SMS permissions are granted at runtime when features are used.

---

## 7) Notification / Foreground Service behavior & debugging
1. After granting permissions, start the assistant via FAB or Home START button; a persistent foreground notification (channel `sana_fg`) should appear.
2. If the service stops unexpectedly:
   - Open Settings → Apps → SANA → Battery → Ensure background/auto-start allowed (repeat steps above).
   - Check App not under App Lock or Power Saver.
   - Ask user to add SANA to the "Protected apps" list if present.

Debug tips: Ask user to open Logcat (via AndroidIDE or adb) and filter for tags: SANA_BRAIN, SANA_VOCAL, SANA_STT, HOME_FRAG. Provide logs when reporting issues.

---

## 8) First-launch permission flow (what to request and when)
1. On first launch, show a friendly onboarding screen (explain in Hinglish):
   "Boss, SANA ko kaam karne ke liye kuch permissions chahiye: Microphone (bolne ke liye), Notifications (message padhne ke liye), Contacts/Calls (call/send messages), Accessibility (screen read/controls)."
2. Request runtime permissions in-context when the feature is first used (don't request everything at once):
   - Microphone: before starting STT loop
   - Notifications listener: open settings screen for Notification Access
   - Accessibility: open settings screen for Accessibility
   - Contacts: before calling/whatsapp
   - Phone/SMS: before call/SMS features
3. After each grant, show a confirmation toast: "Thanks Boss — SANA ready!"

---

## 9) How to test each feature with voice commands (quick checklist)
- Start service: "Hey SANA, start listening" → Notification should appear, status=LISTENING.
- Speak a simple command with expected action: "YouTube pe song chalao — [song]" → Should route to PlayYoutube/PlaySpotify and optionally open YouTube/Spotify.
- Send WhatsApp: "WhatsApp bhejo Rahul: Kaam ho gaya?" → Ensure WhatsApp contact resolution works and wa.me/open intent is correct.
- Read notifications: Send a WhatsApp message to device → SANA should broadcast and speak when live mode on.
- Screen read: "Read screen" while an app is open → Accessibility service must be enabled and SANA should extract text.
- Lock screen: "Lock screen" → Device Admin must be active for lock to succeed.
- Reminders: "Set reminder: Take pills in 10 minutes" → Alarm should schedule and trigger ReminderAlarmReceiver.

---

## Additional troubleshooting & user messaging (Hinglish snippets)
- When a permission is missing, speak friendly error:
  - "Boss, permission nahi mila: Microphone chahiye. Please Allow Microphone in Settings." 
  - "Boss, Accessibility on karo — SANA ko screen padhne ke liye zaroori hai." 
- If OEM kills service:
  - Show an in-app help modal with step-by-step screenshots for that OEM.
  - Provide a "Open Battery Settings" button that launches the battery management screen (use ACTION_POWER_USAGE_SUMMARY or specific intent if available).

---

## Deployment checklist for builds (Developer notes)
- Ensure notification channel `sana_fg` is created on app startup (SanaApp.createNotificationChannel()).
- Keep FOREGROUND_SERVICE and RECORD_AUDIO permissions in manifest; request at runtime for RECORD_AUDIO.
- Document known OEM quirks in README and support flows.

---

## Final note
This guide should be bundled into the app as `docs/oppo_coloros_deployment_guide.md` and shown in-app or in support pages. For OEM-specific screenshots, capture from a representative ColorOS device and include them in the support assets folder.

Good — Phase 9 guide created. Next I will generate Phase 10 (if requested) which will implement any requested UX polish or wire ControlFragment switches to actual IntentRouter features.
