import org.gradle.kotlin.dsl.execution.ProgramText.Companion.from

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("moengageInternal") {
            from("com.moengage:android-dependency-catalog-internal:1.1.0")
        }
        create("moengage") {
            from("com.moengage:android-dependency-catalog:2.5.1")
        }
    }
}
include(":example", ":moengage-segment-integration")

rootProject.name = "moengage-segment"