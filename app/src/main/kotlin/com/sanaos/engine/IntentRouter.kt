package com.sanaos.engine

import android.content.Context
import com.sanaos.engine.features.*

object IntentRouter {
    fun route(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.TorchOn,
            is SanaBrain.SanaIntent.TorchOff,
            is SanaBrain.SanaIntent.TorchToggle -> FlashlightFeature().execute(intent, context)

            is SanaBrain.SanaIntent.SetVolume,
            is SanaBrain.SanaIntent.MuteVolume,
            is SanaBrain.SanaIntent.MaxVolume,
            is SanaBrain.SanaIntent.SetSoundProfile -> VolumeFeature().execute(intent, context)

            is SanaBrain.SanaIntent.SetBrightness,
            is SanaBrain.SanaIntent.AutoBrightness,
            is SanaBrain.SanaIntent.MaxBrightness,
            is SanaBrain.SanaIntent.MinBrightness -> BrightnessFeature().execute(intent, context)

            is SanaBrain.SanaIntent.LockScreen -> ScreenLockFeature().execute(intent, context)
            is SanaBrain.SanaIntent.Screenshot -> ScreenshotFeature().execute(intent, context)
            is SanaBrain.SanaIntent.ScreenRecord -> ScreenRecordFeature().execute(intent, context)
            is SanaBrain.SanaIntent.WifiOn, is SanaBrain.SanaIntent.WifiOff -> WifiFeature().execute(intent, context)
            is SanaBrain.SanaIntent.BluetoothOn, is SanaBrain.SanaIntent.BluetoothOff -> BluetoothFeature().execute(intent, context)
            is SanaBrain.SanaIntent.MobileDataOn, is SanaBrain.SanaIntent.MobileDataOff -> MobileDataFeature().execute(intent, context)
            is SanaBrain.SanaIntent.AirplaneOn, is SanaBrain.SanaIntent.AirplaneOff -> AirplaneModeFeature().execute(intent, context)
            is SanaBrain.SanaIntent.AnswerCall, is SanaBrain.SanaIntent.RejectCall, is SanaBrain.SanaIntent.DialCall -> CallFeature().execute(intent, context)
            is SanaBrain.SanaIntent.SendWhatsApp,
            is SanaBrain.SanaIntent.OpenWhatsApp,
            is SanaBrain.SanaIntent.WhatsAppAudioCall,
            is SanaBrain.SanaIntent.WhatsAppVideoCall -> WhatsAppFeature().execute(intent, context)
            is SanaBrain.SanaIntent.SendSms -> SmsFeature().execute(intent, context)
            is SanaBrain.SanaIntent.OpenTelegram -> TelegramFeature().execute(intent, context)
            is SanaBrain.SanaIntent.PlaySpotify -> SpotifyFeature().execute(intent, context)
            is SanaBrain.SanaIntent.PlayYoutube -> YoutubeFeature().execute(intent, context)
            is SanaBrain.SanaIntent.OpenCamera,
            is SanaBrain.SanaIntent.TakePhoto,
            is SanaBrain.SanaIntent.FlipCamera -> CameraFeature().execute(intent, context)
            is SanaBrain.SanaIntent.NavigateTo,
            is SanaBrain.SanaIntent.SearchNearby,
            is SanaBrain.SanaIntent.ViewOnMap -> MapsFeature().execute(intent, context)
            is SanaBrain.SanaIntent.LaunchApp -> AppLauncherFeature().execute(intent, context)
            is SanaBrain.SanaIntent.QueryBattery -> BatteryFeature().execute(intent, context)
            is SanaBrain.SanaIntent.QueryRam,
            is SanaBrain.SanaIntent.QueryStorage,
            is SanaBrain.SanaIntent.QueryLocation -> BatteryFeature().execute(intent, context)
            is SanaBrain.SanaIntent.QueryWeather -> WeatherFeature().execute(intent, context)
            is SanaBrain.SanaIntent.QueryTime,
            is SanaBrain.SanaIntent.QueryDate,
            is SanaBrain.SanaIntent.QueryNetworkStatus -> NetworkStatusFeature().execute(intent, context)
            is SanaBrain.SanaIntent.ReadScreen -> ScreenReaderFeature().execute(intent, context)
            is SanaBrain.SanaIntent.ScrollDown,
            is SanaBrain.SanaIntent.ScrollUp,
            is SanaBrain.SanaIntent.ScrollToTop,
            is SanaBrain.SanaIntent.ScrollToBottom -> ScrollFeature().execute(intent, context)
            is SanaBrain.SanaIntent.SetReminder -> ReminderFeature().execute(intent, context)
            is SanaBrain.SanaIntent.Converse -> FeatureResult(true, intent.text)
        }
    }
}
