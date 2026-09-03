plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.patsy.app"
    compileSdk = 35
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { buildConfig = true }

    defaultConfig {
        applicationId = "com.patsy.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 339
        versionName = "3.3.9-thynkin-visible1"

        // Client-safe public Supabase configuration only. Never ship service-role/provider secrets.
        buildConfigField("String", "SUPABASE_URL", "\"https://tvtknwqcqbkecszvppub.supabase.co\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"sb_publishable_YF3M02GBd_F3yBxCII33JA_NJtkHq5Y\"")
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
}

dependencies {
    // Official Rive Android runtime. The authored patsy_assistant.riv is supplied separately.
    implementation("app.rive:rive-android:11.9.2") {
        exclude(group = "androidx.compose", module = "compose-bom")
        exclude(group = "androidx.core", module = "core-ktx")
        exclude(group = "androidx.lifecycle", module = "lifecycle-runtime-compose")
        exclude(group = "androidx.lifecycle", module = "lifecycle-runtime-ktx")
    }
    implementation("androidx.startup:startup-runtime:1.2.0")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.media3:media3-exoplayer:1.8.1")
    implementation("androidx.media3:media3-ui:1.8.1")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}
