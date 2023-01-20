plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(moengageInternal.plugins.plugin.dokka)
    alias(moengageInternal.plugins.plugin.android.lib)
    alias(moengageInternal.plugins.plugin.kotlin.android)
}

val libVersionName = project.findProperty("VERSION_NAME") as String

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        multiDexEnabled = true
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-consumer-rules.pro")

        buildConfigField("String", "MOENGAGE_SEGMENT_SDK_VERSION", "\"$libVersionName\"")
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("com.segment.analytics.kotlin:android:1.5.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.core:core-ktx:1.7.0")

    implementation("androidx.lifecycle:lifecycle-process:2.4.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.1")
}

// Partner Dependencies
dependencies {
    api(libs.moengageCore)
}

// Test Dependencies
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation("io.mockk:mockk:1.12.4")

    // Add Roboelectric dependencies.
    testImplementation("org.robolectric:robolectric:4.7.3")
    testImplementation("androidx.test:core:1.4.0")

    // Add JUnit4 legacy dependencies.
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))

    // For JSON Object testing
    testImplementation("org.json:json:20200518")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
}

// Android Test Deps
dependencies {
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
