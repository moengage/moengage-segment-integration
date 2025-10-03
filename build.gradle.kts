@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(moengageInternal.plugins.plugin.android.app) apply false
    alias(moengageInternal.plugins.plugin.android.lib) apply false
    alias(moengageInternal.plugins.plugin.kotlin.android) apply false
    alias(moengageInternal.plugins.plugin.dokka) apply false
    alias(libs.plugins.plugin.google.play.service) apply false
    alias(moengageInternal.plugins.plugin.kotlin.serialization) apply false
    alias(moengageInternal.plugins.plugin.release) apply false
    alias(libs.plugins.spotless)
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.diffplug.spotless")

    spotless {
        ratchetFrom = "origin/development"
        kotlin {
            ktfmt("0.56").kotlinlangStyle().configure {
                it.setMaxWidth(100)
                it.setManageTrailingCommas(false)
                it.setRemoveUnusedImports(true)
            }
            target("src/**/*.kt") // Specify which files to format
            targetExclude("**/build/**") // Exclude files in build directories
            licenseHeaderFile("../License.txt")
        }
    }
}

apply(plugin = "org.jetbrains.dokka")
