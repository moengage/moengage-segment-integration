enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("moengage") {
            from("com.moengage:android-dependency-catalog:2.0.0")
        }
        create("moengageInternal") {
            from("com.moengage:android-dependency-internal:0.0.3")
        }
    }
}
include(":example", ":moengage-segment-integration")

rootProject.name = "moengage-segment"