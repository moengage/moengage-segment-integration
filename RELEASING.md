### Releasing

 1. Change the version name in `gradle.properties`.(version name- X.Y.Z)
 2. Update the `CHANGELOG.md` for the impending release.
 3. Run the following command `./gradlew moengage-segment-integration:publish --no-daemon --no-parallel`
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 5. `git tag -a X.Y.Z -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 6. `git push && git push --tags`
