# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep data classes for serialization
-keep class de.grunert.replyhelper.data.** { *; }

# Keep domain models
-keep class de.grunert.replyhelper.domain.** { *; }



