# Suppress warnings related to platform-specific WindowInsets methods in Compose
# These methods may only be available on certain platforms (e.g., Android or Skiko)
-dontwarn androidx.compose.foundation.layout.WindowInsets_notMobileKt
-dontwarn androidx.compose.foundation.layout.WindowInsets$Companion

# Suppress missing method/class warnings for Material and Material3 Compose libraries
-dontwarn androidx.compose.material.**
-dontwarn androidx.compose.material3.**

# Suppress warnings for platform-specific or optional SSL/security libraries
-dontwarn dalvik.system.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn android.**

# Keep Kotlin metadata classes required for reflection and annotations
-keep class kotlin.Metadata { *; }

# Keep all kotlinx.* classes (e.g., kotlinx.serialization, kotlinx.coroutines)
-keep class kotlinx.** { *; }

# Keep all androidx.* classes (Jetpack Compose UI components)
-keep class androidx.** { *; }
