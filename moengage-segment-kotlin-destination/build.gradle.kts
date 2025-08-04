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
    compileSdk = moengageInternal.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = moengageInternal.versions.minSdk.get().toInt()

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
        targetSdk = moengageInternal.versions.targetSdk.get().toInt()
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    compileOnly(libs.segmentKotlin)
    api(libs.moengageCore)
    testImplementation(moengageInternal.bundles.junit5)
    testImplementation(moengageInternal.jsonAssert)
    testImplementation(moengageInternal.kotlinStdLib)
    testImplementation(moengageInternal.kotlinSerialization)
    testImplementation(libs.segmentKotlin)
    testImplementation(libs.moengageCore)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
