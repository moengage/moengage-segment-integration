plugins {
    alias(moengageInternal.plugins.plugin.dokka)
    alias(moengageInternal.plugins.plugin.android.lib)
    alias(moengageInternal.plugins.plugin.kotlin.android)
    alias(moengageInternal.plugins.plugin.kotlin.serialization)
    alias(moengageInternal.plugins.plugin.release)
}

val libVersionName = project.findProperty("VERSION_NAME") as String

android {
    namespace = "com.segment.analytics.kotlin.destinations.moengage"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

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
    lint {
        targetSdk = 33
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    compileOnly(libs.segmentKotlin)
    api(libs.moengageCore)

    testImplementation(moengageInternal.bundles.junitBundle)
    testImplementation(moengageInternal.kotlinStdLib)
    testImplementation(libs.moengageCore)
    testImplementation(libs.segmentKotlin)
    testImplementation(moengageInternal.kotlinSerialization)
}
