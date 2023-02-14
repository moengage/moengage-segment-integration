import java.util.Properties
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 33

    val properties = Properties()
    properties.load(file("../local.properties").inputStream())

    defaultConfig {
        applicationId = "in.co.kotlinsampleapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SEGMENT_WRITE_KEY", "\"${properties.getProperty("segmentWriteKey")}\"")
        buildConfigField("String", "MOENAGE_APP_ID", "\"${properties.getProperty("moengageAppId")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.lifecycle:lifecycle-process:2.4.0")
    implementation("com.segment.analytics.kotlin:android:1.10.0")
    implementation(projects.moengageSegmentKotlinDestination)
    implementation(moengage.inapp)
    implementation(moengageInternal.kotlinStdLib)
    implementation("com.google.firebase:firebase-messaging:23.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}