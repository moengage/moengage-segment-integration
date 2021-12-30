buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(moengageInternal.bundles.gradlePlugins)
        classpath("com.google.gms:google-services:4.3.10")
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}