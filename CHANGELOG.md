### 5.3.01
- MoEngage SDK version updated to `11.2.01`, [Release Notes](https://docs.moengage.com/docs/android-release-notes#11201-13-05-2021)

### 5.3.00 (10-05-2021)
- MoEngage SDK version updated to `11.2.00`, [Release Notes](https://docs.moengage.com/docs/android-release-notes#11200-10-05-2021) 

### 5.2.00 (14-04-2021)
- MoEngage SDK version updated to `11.1.00`, [Release Notes](https://docs.moengage.com/docs/android-release-notes#11100-14-04-2021) 

### 5.1.01 (08-04-2021)
- MoEngage SDK version updated to `11.0.05`, [Release Notes](https://docs.moengage.com/docs/android-release-notes#11005-08-04-2021) 

### 5.1.00 (01-03-2021)
- MoEngage SDK version pinned to a specific version. `11.0.04` in this release. 

### 5.0.01 (24-02-2021)
- Updating minimum dependency of MoEngage SDK to version published on maven central
- Publishing moved to Maven Central. All future updates would be published on Maven Central only.

### 5.0.00 (28-01-2021)
- MoEngage SDK version updated to support `11.0.03` and above. Refer to the [Release Notes](https://docs.moengage.com/docs/android-release-notes#11003-28-01-2021) for more details.

### 4.3.00 (19-11-2020)
- MoEngage SDK version is no longer pinned to a specific version. Latest version within the defined range would be
 picked. In the defined range major version of the SDK is maintained.

### 4.2.03(05-11-2020)
- Updated MoEngage SDK version to `10.4.04`

### 4.2.02(16-10-2020)
- Updated MoEngage SDK version to `10.4.03`

### 4.2.01(25-09-2020)
- Updated MoEngage SDK version to `10.4.01`

### 4.2.00(17-09-2020)
- Updated MoEngage SDK version to `10.4.00`. Refer to the [Release Notes](https://docs.moengage.com/docs/android-release-notes#v10400) for more details.
- Moved to `androidx` namespace.

### v4.1.01(07-07-2020)
- Updated MoEngage SDK version to 10.1.01
    
### v4.1.00(02-07-2020)
- Updated MoEngage SDK version to 10.1.00
    - Cards support

### v4.0.03(05-04-2020)
- Updated MoEngage SDK version to 10.0.03
    - Migrated integration-verification module to Kotlin

### v4.0.02(18-03-2020)
- Updated MoEngage SDK version to 10.0.02
    - Support added for India Cluster
    
### v4.0.01 (04-03-2020)
- Updated MoEngage SDK version to 10.0.01

### v4.0.00 (21-02-2020)
- Removed Support for manifest based integration
- Removed support for MoEngage's GCM library. If you are still using the GCM dependency move to FCM dependency.
- Removed support for Fresco, use Glide instead if you are using Gifs for In-Apps.
- If sender id is provided while initializing the SDK it will be used for token registration instead of the default sender id in the google-services.json file.
- InApp Callbacks - InApp Callbacks listener is now a concrete class rather than an interface. Refer to the API documentation for more details.
- Self-Handled in-app delivered on the Main thread
Refer to [Migration Guide](doc:migration-to-10xxx)  documentation for more details.

### v3.5.03 (14-02-2020)
- Updated MoEngage SDK version to 9.8.04

### v3.5.03 (04-02-2020)
- Updated MoEngage SDK version to 9.8.03

### v3.5.02 (12-01-2020)
- Updated MoEngage SDK version to 9.8.02