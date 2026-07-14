# ─────────────────────────────────────────────────────────────
# CCloud ProGuard Rules
# ─────────────────────────────────────────────────────────────

# ── Preserve line numbers for crash stack traces ─────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── ExoPlayer / Media3 ───────────────────────────────────────
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ── Kotlinx Serialization ────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}
-keepclasseswithmembers class **$$serializer {
    *** INSTANCE;
}

# Keep all serializable model classes
-keep class com.pira.ccloud.data.model.** { *; }
-keep class com.pira.ccloud.data.remote.response.** { *; }

# ── OkHttp ───────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ── Hilt / Dependency Injection (preparation for Phase 2) ────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ── Compose ──────────────────────────────────────────────────
-dontwarn androidx.compose.**

# ── Coroutines ───────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ── Keep BuildConfig for API key access ──────────────────────
-keep class com.pira.ccloud.BuildConfig { *; }
