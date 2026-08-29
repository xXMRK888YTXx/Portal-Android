# BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
-keep class javax.crypto.** { *; }
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.BouncyCastleProvider { *; }

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

# Keep all data models exchanged over Network, Database, and Wear Data Layer
-keep class com.xxmrk888ytxx.portal.data.model.** { *; }
-keep class com.xxmrk888ytxx.portal.data.wear.** { *; }
-keep class com.xxmrk888ytxx.portal.domain.model.** { *; }

# Google Play Services Wearable and Services/Receivers
-keep class com.google.android.gms.wearable.** { *; }
-keep class com.xxmrk888ytxx.portal.data.service.WearPhoneListenerService { *; }
-keep class com.xxmrk888ytxx.portal.data.service.** { *; }
-keep class com.xxmrk888ytxx.portal.data.broadcastReceiver.** { *; }