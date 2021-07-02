plugins {
    id("com.android.library")
    id("org.jetbrains.dokka")
}

ext {
    set(PomKeys.artifactId, ReleaseConfig.artifactId)
    set(PomKeys.description, ReleaseConfig.description)
    set(PomKeys.name, ReleaseConfig.artifactName)
    set(PomKeys.versionName, ReleaseConfig.versionName)
}

android {
    compileSdkVersion(SdkBuildConfig.compileSdkVersion)

    defaultConfig {

        minSdkVersion(SdkBuildConfig.minimumSdkVersion)
        targetSdkVersion(SdkBuildConfig.targetSdkVersion)

        buildConfigField(
          "String",
          "MOENGAGE_SEGMENT_SDK_VERSION",
          "\"${ReleaseConfig.versionName}\""
        )
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
    compileOnly(Deps.segment)
    api(Deps.moengage)
}

apply(plugin= "com.vanniktech.maven.publish")