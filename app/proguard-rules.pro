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

# Keep data classes for serialization
-keep class de.grunert.wasantwort.data.** { *; }

# Keep domain models (enums)
-keep class de.grunert.wasantwort.domain.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# DataStore
-keep class androidx.datastore.preferences.** { *; }
