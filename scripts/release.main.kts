#!/usr/bin/env kotlin

@file:Import("../../sdk-automation-scripts/scripts/android-release-utils.main.kts")
@file:Import("../../sdk-automation-scripts/scripts/common-utils.main.kts")
@file:Import("../../sdk-automation-scripts/scripts/git-utils.main.kts")

private val kotlinDestinationModuleName = "moengage-segment-kotlin-destination"
private val releaseBranch = "master"

// Pre-release
executeCommandOnShell("git checkout development")
executeCommandOnShell("git pull origin development")
executeCommandOnShell("git checkout master")
executeCommandOnShell("git pull origin master")
executeCommandOnShell("git checkout development")
executeCommandOnShell("git merge master")
executeCommandOnShell("git checkout master")
executeCommandOnShell("git merge development")
executeCommandOnShell("git push origin master")

// Release
val doesReleaseRequired = getChangedFilesAfterLastRelease(releaseBranch).filter {
    it.startsWith(kotlinDestinationModuleName)
}.size > 0

if (doesReleaseRequired) {
    releaseKotlinDestination()
}

fun releaseKotlinDestination() {
    println("Releasing kotlin destination")
    if (releaseModules(listOf(kotlinDestinationModuleName)).size == 1) {
        val releaseVersion = getVersionNameForModule(kotlinDestinationModuleName, true)
        tagModuleWithLatestVersion(kotlinDestinationModuleName, kotlinDestinationModuleName)
        pushLocalTags()
        println("catalog released with $releaseVersion")
    }
}