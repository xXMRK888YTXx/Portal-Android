# Сохраняем все классы BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Также важно сохранить интерфейсы JCE, если они используются
-keep class javax.crypto.** { *; }

# Если вы используете специфические конвертеры BouncyCastle
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.BouncyCastleProvider { *; }