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
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    versionCatalogs {
        create("moengageInternal") {
            from("com.moengage:android-dependency-catalog-internal:1.2.1-SNAPSHOT")
        }
        create("moengage") {
            from("com.moengage:android-dependency-catalog:2.5.1")
        }
    }
}
include(":example", ":moengage-segment-integration")

rootProject.name = "moengage-segment"