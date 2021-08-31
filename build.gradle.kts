buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.13.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}