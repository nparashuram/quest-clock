# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep only essential classes
-keep class com.nparashuram.quest.clock.** { *; }

# Remove all logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove unused resources and warnings
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn android.view.animation.**
-dontwarn android.animation.**
-dontwarn android.transition.**
-dontwarn android.app.**
-dontwarn android.content.**
-dontwarn android.os.**
-dontwarn android.widget.**
-dontwarn android.view.**

# Aggressive optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 8
-allowaccessmodification
-dontpreverify

# Remove unused attributes
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses

# Keep only necessary AppCompat components
-keep class androidx.appcompat.app.AppCompatActivity { *; }
-keep class androidx.appcompat.app.AppCompatDelegate { *; }

# Remove unused methods and fields
-assumenosideeffects class java.lang.System {
    public static void gc();
    public static void runFinalization();
} 