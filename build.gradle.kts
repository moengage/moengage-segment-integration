@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(moengageInternal.plugins.plugin.android.app) apply false
    alias(moengageInternal.plugins.plugin.android.lib) apply false
    alias(moengageInternal.plugins.plugin.kotlin.android) apply false
    alias(moengageInternal.plugins.plugin.dokka) apply false
    alias(libs.plugins.plugin.google.play.service) apply false
    alias(moengageInternal.plugins.plugin.kotlin.serialization) apply false
    alias(moengageInternal.plugins.plugin.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

apply(plugin = "org.jetbrains.dokka")
