# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serializers for all data classes
-keep,includedescriptorclasses class de.grunert.wasantwort.**$$serializer { *; }
-keepclassmembers class de.grunert.wasantwort.** {
    *** Companion;
}
-keepclasseswithmembers class de.grunert.wasantwort.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data classes for serialization (only DTOs and Settings)
-keep class de.grunert.wasantwort.data.AiDtos { *; }
-keep class de.grunert.wasantwort.data.ChatMessage { *; }
-keep class de.grunert.wasantwort.data.ChatCompletionRequest { *; }
-keep class de.grunert.wasantwort.data.ChatCompletionResponse { *; }
-keep class de.grunert.wasantwort.data.Choice { *; }
-keep class de.grunert.wasantwort.data.MessageContent { *; }
-keep class de.grunert.wasantwort.data.AppSettings { *; }

# Keep domain models (enums and serializable classes only)
-keep enum de.grunert.wasantwort.domain.** { *; }
-keep class de.grunert.wasantwort.domain.ConversationEntry { *; }
-keep class de.grunert.wasantwort.domain.ParseResult { *; }

# Ktor (more specific rules to avoid over-keeping)
-keep class io.ktor.client.** { *; }
-keep class io.ktor.http.** { *; }
-keep class io.ktor.util.** { *; }
-dontwarn io.ktor.**

# DataStore
-keep class androidx.datastore.preferences.** { *; }

# SLF4J - ignore missing bindings (we don't use SLF4J logging on Android)
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder

# Java Management - ignore JVM-only classes not available on Android
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Compose UI Components - Only keep entry points (R8 handles the rest)
-keep class de.grunert.wasantwort.ui.MainScreenKt { *; }
-keep class de.grunert.wasantwort.ui.SettingsScreenKt { *; }
-keep class de.grunert.wasantwort.ui.HistoryScreenKt { *; }

# Keep Composable functions (R8 optimized)
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep ViewModel (necessary for reflection and lifecycle)
-keep class de.grunert.wasantwort.viewmodel.MainViewModel { *; }
-keep class de.grunert.wasantwort.viewmodel.MainScreenState { *; }
-keep class de.grunert.wasantwort.viewmodel.MainUiState { *; }
-keep class de.grunert.wasantwort.viewmodel.ErrorSource { *; }

# Compose Runtime (let R8 optimize, only keep what's needed)
-dontwarn androidx.compose.runtime.**
-dontwarn androidx.compose.ui.**

# Keep all lambda expressions (important for onClick handlers)
-keepclassmembers class * {
    *** lambda$*(...);
}

# Keep all kotlin.jvm.functions interfaces (for lambda parameters)
-keep class kotlin.jvm.functions.** { *; }

# Security - Encrypted Shared Preferences
-keep class androidx.security.crypto.** { *; }
-keepclassmembers class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# Keep EncryptedKeyStore (for API key security)
-keep class de.grunert.wasantwort.data.EncryptedKeyStore { *; }
-keepclassmembers class de.grunert.wasantwort.data.EncryptedKeyStore { *; }

# Tink - ignore unused dependencies
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn org.joda.time.Instant




