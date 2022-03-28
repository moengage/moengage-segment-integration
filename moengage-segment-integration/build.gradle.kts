import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("kotlin-android")
}

fun getVersionName(): String {
    val properties = Properties()
    properties.load(FileInputStream("${project.rootDir.absolutePath}/moengage-segment-integration/gradle.properties"))
    return properties.getProperty("VERSION_NAME") ?: throw GradleException(
        "VERSION_NAME not found in ./moengage-segment-integration/gradle.properties"
    )
}


android {
    compileSdk = 30
    defaultConfig {
        minSdk = 21
        targetSdk = 30

        buildConfigField("String", "MOENGAGE_SEGMENT_SDK_VERSION", "\"${getVersionName()}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly(libs.segment)
    api(libs.moengageCore)
}

apply(plugin = "com.vanniktech.maven.publish")