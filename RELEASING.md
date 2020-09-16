### Releasing

 1. Change the version name and version code in `configuration.gradle`.(version name- X.Y.Z
 , version code- XYZ)
 2. Update the `CHANGELOG.md` for the impending release.
 3. Navigate to `moengage-segment-integration` and run the following command `../gradlew clean
  build install bintrayUpload`
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 5. `git tag -a X.Y.Z -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 6. `git push && git push --tags`
