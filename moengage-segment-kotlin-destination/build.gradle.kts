import com.moengage.gradle.android.library.plugin.configs.BuildConfigType

plugins {
    alias(libs.plugins.plugin.native.module.config)
}

moduleConfig.configure {
    buildFeature {
        buildConfigField(
            BuildConfigType.STRING,
            "MOENGAGE_SEGMENT_KOTLIN_VERSION",
            "\"${project.findProperty("VERSION_NAME") as String}\""
        )
    }
}

android {
    namespace = "com.segment.analytics.kotlin.destinations.moengage"
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
