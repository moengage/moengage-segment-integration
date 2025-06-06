import java.util.Properties
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 35
    namespace = "com.moengage.segment.kotlin.sampleapp"
    val properties = Properties()
    properties.load(file("../local.properties").inputStream())

    defaultConfig {
        applicationId = "com.moengage.segment.kotlin.sampleapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SEGMENT_WRITE_KEY", "\"${properties.getProperty("segmentWriteKey")}\"")
        buildConfigField("String", "MOENGAGE_WORKSPACE_ID", "\"${properties.getProperty("moengageWorkspaceId")}\"")
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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(projects.moengageSegmentKotlinDestination)

    implementation(moengage.androidXCore)
    implementation(moengage.androidXCompact)
    implementation(moengage.androidXLifecycle)
    implementation(moengage.inapp)
    implementation(moengage.firebaseMessaging)
    implementation(moengageInternal.kotlinStdLib)

    implementation(libs.material)
    implementation(libs.segmentKotlin)
}
