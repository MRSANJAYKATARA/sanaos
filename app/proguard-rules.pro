-keep class com.sanaos.** { *; }
-keepclassmembers class com.sanaos.** { *; }
-keepattributes *Annotation*
# Keep OkHttp & Moshi/Gson models used via reflection
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
