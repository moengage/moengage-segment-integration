@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(moengageInternal.plugins.plugin.android.app) apply false
    alias(moengageInternal.plugins.plugin.android.lib) apply false
    alias(moengageInternal.plugins.plugin.kotlin.android) apply false
    alias(moengageInternal.plugins.plugin.dokka) apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
}

buildscript{
    dependencies{
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

apply(plugin = "org.jetbrains.dokka")
