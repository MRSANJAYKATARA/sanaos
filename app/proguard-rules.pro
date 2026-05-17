# SANA PRO 2.0 ProGuard Rules
-keep class com.sanaos.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
