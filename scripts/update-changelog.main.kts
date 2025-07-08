#!/usr/bin/env kotlin

@file:Import("../../sdk-automation-scripts/scripts/common/file_updater.main.kts")

val releaseNotes = args[0]
val versionNumber = args[1]

if (releaseNotes.isBlank()) {
    throw IllegalArgumentException("Release notes cannot be empty")
}

if (versionNumber.isBlank()) {
    throw IllegalArgumentException("Version cannot be empty")
}

val changelogEntry = """
    # Release Date
    
    ## Release Version
    
    - Release notes [here]($releaseNotes)
    - MoEngage SDK version updated to `$versionNumber`
""".trimIndent()
insertContentInFileAfterPattern(pattern = "# moengage-segment-kotlin-destination", content = changelogEntry, filePath = "./CHANGELOG.md")