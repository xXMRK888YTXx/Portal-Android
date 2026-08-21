# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Keep all Serializable classes, their fields, companion objects, and generated serializers
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers enum * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers enum * {
    @kotlinx.serialization.SerialName *;
}
-keep,allowobfuscation,allowshrinking class * extends kotlinx.serialization.internal.GeneratedSerializer {
    <init>(...);
}
-keepclassmembers class * extends kotlinx.serialization.internal.GeneratedSerializer {
    <fields>;
    <methods>;
}

# Keep Wear Data Layer Protocol models and services
-keep class com.xxmrk888ytxx.portal.data.** { *; }
-keep class com.xxmrk888ytxx.portal.domain.model.** { *; }
-keep class com.google.android.gms.wearable.** { *; }
-keep class com.xxmrk888ytxx.portal.data.service.WearPortalListenerService { *; }
-keep class com.xxmrk888ytxx.portal.data.broadcastReceiver.** { *; }
