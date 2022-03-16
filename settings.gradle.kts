enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("moengage") {
            from("com.moengage:android-dependency-catalog:2.0.0")
        }
        create("moengageInternal") {
            from("com.moengage:android-dependency-catalog-internal:1.0.0")
        }
    }
}
include(":example", ":moengage-segment-integration")

rootProject.name = "moengage-segment"