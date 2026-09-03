# Android Studio JDK 21 pin

This branch adds `gradle/gradle-daemon-jvm.properties` with `toolchainVersion=21` so Gradle 8.10.2 launches its daemon with Java 21 even if Android Studio itself is running on a newer bundled runtime.

Purpose: eliminate the repeated JVM 25/Gradle 8.10.2 incompatibility on Windows while preserving the existing Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Java/Kotlin compile target 17, and application code unchanged.
