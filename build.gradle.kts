@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(moengageInternal.plugins.plugin.android.app) apply false
    alias(moengageInternal.plugins.plugin.android.lib) apply false
    alias(moengageInternal.plugins.plugin.kotlin.android) apply false
    alias(moengageInternal.plugins.plugin.dokka) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    alias(moengageInternal.plugins.plugin.kotlin.serialization) apply false
    alias(moengageInternal.plugins.plugin.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

apply(plugin = "org.jetbrains.dokka")
