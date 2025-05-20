# Release Process

- Merge all the releasing changes into `development`.
- Update the `moe-android-sdk` and `android-dependency-catalog-internal` version in `development`.
- Push the changes to remote and run the `Release` action on the `development` branch.

Note: The release workflow takes care of updating the release version and the dates in the changelog file. Do not update manually.