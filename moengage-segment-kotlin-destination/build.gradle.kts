plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(moengageInternal.plugins.plugin.dokka)
    alias(moengageInternal.plugins.plugin.android.lib)
    alias(moengageInternal.plugins.plugin.kotlin.android)
}

apply(from = "../scripts/gradle/release.gradle")

val libVersionName = project.findProperty("VERSION_NAME") as String

android {
    namespace = "com.segment.analytics.kotlin.destinations.moengage"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        buildConfigField("String", "MOENGAGE_SEGMENT_KOTLIN_VERSION", "\"$libVersionName\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly(libs.segmentKotlin)
    api(libs.moengageCore)
}
