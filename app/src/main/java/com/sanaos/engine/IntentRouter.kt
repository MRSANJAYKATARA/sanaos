package com.sanaos.engine

import android.content.Context
import android.util.Log
import com.sanaos.engine.features.*

class IntentRouter(private val context: Context) {

    private val flashlightFeature by lazy { FlashlightFeature(context) }
    private val volumeFeature by lazy { VolumeFeature(context) }
    private val brightnessFeature by lazy { BrightnessFeature(context) }
    private val batteryFeature by lazy { BatteryFeature(context) }
    private val systemInfoFeature by lazy { SystemInfoFeature(context) }
    private val wifiFeature by lazy { WifiFeature(context) }
    private val bluetoothFeature by lazy { BluetoothFeature(context) }
    private val mobileDataFeature by lazy { MobileDataFeature(context) }
    private val airplaneModeFeature by lazy { AirplaneModeFeature(context) }
    private val networkStatusFeature by lazy { NetworkStatusFeature(context) }
    private val spotifyFeature by lazy { SpotifyFeature(context) }
    private val youtubeFeature by lazy { YoutubeFeature(context) }
    private val cameraFeature by lazy { CameraFeature(context) }
    private val appLauncherFeature by lazy { AppLauncherFeature(context) }
    private val callFeature by lazy { CallFeature(context) }
    private val whatsappFeature by lazy { WhatsAppFeature(context) }
    private val telegramFeature by lazy { TelegramFeature(context) }
    private val smsFeature by lazy { SmsFeature(context) }
    private val mapsFeature by lazy { MapsFeature(context) }
    private val weatherFeature by lazy { WeatherFeature(context) }
    private val reminderFeature by lazy { ReminderFeature(context) }
    private val screenLockFeature by lazy { ScreenLockFeature(context) }
    private val screenshotFeature by lazy { ScreenshotFeature(context) }
    private val screenReaderFeature by lazy { ScreenReaderFeature(context) }
    private val scrollFeature by lazy { ScrollFeature(context) }
    private val screenRecordFeature by lazy { ScreenRecordFeature(context) }

    fun route(intent: SanaIntent): FeatureResult {
        return try {
            when (intent) {
                // Torch/Flashlight
                is SanaIntent.TorchOn -> flashlightFeature.torchOn()
                is SanaIntent.TorchOff -> flashlightFeature.torchOff()
                is SanaIntent.TorchToggle -> flashlightFeature.torchToggle()

                // Volume
                is SanaIntent.SetVolume -> volumeFeature.setVolume(intent.percent)
                is SanaIntent.MuteVolume -> volumeFeature.muteVolume()
                is SanaIntent.MaxVolume -> volumeFeature.maxVolume()

                // Brightness
                is SanaIntent.SetBrightness -> brightnessFeature.setBrightness(intent.percent)
                is SanaIntent.AutoBrightness -> brightnessFeature.autoBrightness()
                is SanaIntent.MaxBrightness -> brightnessFeature.maxBrightness()
                is SanaIntent.MinBrightness -> brightnessFeature.minBrightness()

                // Screen Control
                is SanaIntent.LockScreen -> screenLockFeature.lockScreen()
                is SanaIntent.Screenshot -> screenshotFeature.takeScreenshot()
                is SanaIntent.ScreenRecord -> screenRecordFeature.toggleScreenRecord()

                // Connectivity
                is SanaIntent.WifiOn -> wifiFeature.wifiOn()
                is SanaIntent.WifiOff -> wifiFeature.wifiOff()
                is SanaIntent.BluetoothOn -> bluetoothFeature.bluetoothOn()
                is SanaIntent.BluetoothOff -> bluetoothFeature.bluetoothOff()
                is SanaIntent.MobileDataOn -> mobileDataFeature.mobileDataOn()
                is SanaIntent.MobileDataOff -> mobileDataFeature.mobileDataOff()
                is SanaIntent.AirplaneOn -> airplaneModeFeature.airplaneOn()
                is SanaIntent.AirplaneOff -> airplaneModeFeature.airplaneOff()

                // Calls
                is SanaIntent.AnswerCall -> callFeature.answerCall()
                is SanaIntent.RejectCall -> callFeature.rejectCall()
                is SanaIntent.DialCall -> callFeature.dialCall(intent.contact)

                // WhatsApp
                is SanaIntent.SendWhatsApp -> whatsappFeature.sendMessage(intent.contact, intent.message)
                is SanaIntent.OpenWhatsApp -> whatsappFeature.openWhatsApp()
                is SanaIntent.WhatsAppAudioCall -> whatsappFeature.audioCall(intent.contact)
                is SanaIntent.WhatsAppVideoCall -> whatsappFeature.videoCall(intent.contact)

                // SMS
                is SanaIntent.SendSms -> smsFeature.sendSms(intent.contact, intent.message)

                // Telegram
                is SanaIntent.OpenTelegram -> telegramFeature.openTelegram(intent.username)

                // Media
                is SanaIntent.PlaySpotify -> spotifyFeature.playSong(intent.query)
                is SanaIntent.PlayYoutube -> youtubeFeature.playVideo(intent.query)

                // Camera
                is SanaIntent.OpenCamera -> cameraFeature.openCamera()
                is SanaIntent.TakePhoto -> cameraFeature.takePhoto()
                is SanaIntent.FlipCamera -> cameraFeature.flipCamera()

                // Navigation & Maps
                is SanaIntent.NavigateTo -> mapsFeature.navigateTo(intent.destination)
                is SanaIntent.SearchNearby -> mapsFeature.searchNearby(intent.type)
                is SanaIntent.ViewOnMap -> mapsFeature.viewOnMap(intent.query)

                // App Launcher
                is SanaIntent.LaunchApp -> appLauncherFeature.launchApp(intent.appName)

                // System Info Queries
                is SanaIntent.QueryBattery -> batteryFeature.queryBattery()
                is SanaIntent.QueryRam -> systemInfoFeature.queryRam()
                is SanaIntent.QueryStorage -> systemInfoFeature.queryStorage()
                is SanaIntent.QueryLocation -> systemInfoFeature.queryLocation(context)
                is SanaIntent.QueryWeather -> weatherFeature.queryWeather()
                is SanaIntent.QueryTime -> batteryFeature.queryTime()
                is SanaIntent.QueryDate -> batteryFeature.queryDate()
                is SanaIntent.QueryNetworkStatus -> networkStatusFeature.queryNetworkStatus()

                // Screen Reading & Scrolling
                is SanaIntent.ReadScreen -> screenReaderFeature.readScreen()
                is SanaIntent.ScrollDown -> scrollFeature.scrollDown()
                is SanaIntent.ScrollUp -> scrollFeature.scrollUp()
                is SanaIntent.ScrollToTop -> scrollFeature.scrollToTop()
                is SanaIntent.ScrollToBottom -> scrollFeature.scrollToBottom()

                // Reminders
                is SanaIntent.SetReminder -> reminderFeature.setReminder(intent.label, intent.naturalTimeText)

                // Fallback: Converse
                is SanaIntent.Converse -> FeatureResult(true, intent.reply)
            }
        } catch (e: Exception) {
            Log.e("INTENT_ROUTER", "Routing error for intent $intent: ${e.message}", e)
            FeatureResult(false, "Boss, kuch gdbad ho gaya. Dobara koshish kar.")
        }
    }
}
