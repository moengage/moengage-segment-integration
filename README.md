![Logo](/.github/logo.png)

analytics-kotlin-destination-moengage
======================================
To use MoEngage in an Android app, you must perform the following steps to set up your environment.

![MavenBadge](https://img.shields.io/maven-central/v/com.moengage/moengage-segment-kotlin-destination)

> [!Note]
> **Note:** `analytics-android-integration-moengage` will no longer receive updates. We recommend that you use `analytics-kotlin-destination-moengage`. For more details, refer [moengage-segment-android-integration](https://partners.moengage.com/hc/en-us/articles/4409143474964-Android-device-mode-)

### Adding the MoEngage Dependency

Along with the Segment dependency, add the below dependency in your `app/build.gradle` file.

```groovy
   implementation("com.moengage:moengage-segment-kotlin-destination:$sdkVersion")   
```
Replace `$sdkVersion` with the appropriate SDK version

The MoEngage SDK depends on the following Jetpack libraries provided by Google for its functioning. Make you add them if not
 done already.

```groovy
    implementation("androidx.core:core:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.lifecycle:lifecycle-process:2.4.0")
```
For more about the build config and other libraries used by the SDK, refer to the [SDK Configuration] (https://developers.moengage.com/hc/en-us/articles/4401984733972-Android-SDK-Configuration) documentation.

### Register MoEngage with Segment SDK

After adding the dependency, you must register the destination with Segment SDK. To do this, import the MoEngage
 destination:

```kotlin
import com.segment.analytics.kotlin.destinations.moengage.MoEngageDestination
```

Add the following line:

```kotlin
val analytics = Analytics("write_key", this)
analytics.add(MoEngageDestination(this))
```


### Initialize the MoEngage SDK

Copy the Workspace ID from the Settings Page `Dashboard --> Settings --> App --> General` and initialize the MoEngage SDK in the `onCreate()` method of the `Application` class

> [!Note]
> MoEngage recommends initializing the SDK on the main thread inside `onCreate()` and not creating a worker thread and initializing the SDK on that thread.

```kotlin
//This is the instance of the application class, and "XXXXXXXXXXX" is the Workspace ID from the dashboard.
val moEngage = MoEngage.Builder(this, "XXXXXXXXXXX")
       .enablePartnerIntegration(IntegrationPartner.SEGMENT)
       .build()
MoEngage.initialiseDefaultInstance(moEngage)
```
### Exclude MoEngage Storage File from Auto-Backup
The auto backup service of Android periodically backs up the Shared Preference file, Database files, and so on.

If you would like more information, please refer to [Auto Backup](https://developer.android.com/guide/topics/data/autobackup) documentation.

As a result of the backup, MoEngage SDK identifiers are backed up and restored after reinstall.
The restoration of the identifier corrupts your data and prevents the user from being reachable using push notifications.

To ensure data isn't corrupted after a backup is restored, opt out of MoEngage SDK storage files.

Refer to the [documentation](https://developers.moengage.com/hc/en-us/articles/4401999257236-Exclude-MoEngage-Storage-File-from-Auto-Backup) for further details.

### Install or update differentiation
This is required for migrations to the MoEngage Platform so the SDK can determine whether the user is a new user on your app or an existing user who updated to the latest version.

If the user was already using your application and has just updated to a new version that has the MoEngage SDK, below is an example call:

```kotlin
 MoEAnalyticsHelper.setAppStatus(context, AppStatus.UPDATE);
```

If this is a fresh install:

```kotlin
MoEAnalyticsHelper.setAppStatus(context, AppStatus.INSTALL);
```

### Configure Push Notifications

#### Add meta information for push notification

Some metadata about the notification is required to display push notifications. For example, the small icon and large icon drawable are mandatory.


Refer to the [MoEngage - NotificationConfig](https://moengage.github.io/android-api-reference-v11/core/com.moengage.core.config/-notification-config/index.html) API reference for all the possible options.


Use the `configureNotificationMetaData()` to pass on the configuration to the SDK.


```kotlin
val moEngage = MoEngage.Builder(this, "XXXXXXXX")
     .configureNotificationMetaData(NotificationConfig(R.drawable.small_icon, R.drawable.large_icon)) 
     .enablePartnerIntegration(IntegrationPartner.SEGMENT)
     .build()
     
MoEngage.initialiseDefaultInstance(moEngage)
```

#### Configuring Firebase Cloud Messaging

For showing Push notifications, there are two essential steps:

1. Registration for Push, generating a push token.
2. Receive the Push payload from the Firebase Cloud Messaging(FCM) service and show the notification on the device.

##### The application handles push registration and receiving

###### Opt-out of MoEngage registration

To opt out of the MoEngage token registration mechanism, disable token registration while configuring FCM in the `MoEngage.Builder` as shown below.

```kotlin
val moEngage = MoEngage.Builder(this, "XXXXXXXX")
    .enablePartnerIntegration(IntegrationPartner.SEGMENT)
    .configureNotificationMetaData(NotificationConfig(R.drawable.small_icon, R.drawable.large_icon, R.color.notiColor, null, true, isBuildingBackStackEnabled = false, isLargeIconDisplayEnabled = true))
    .configureFcm(FcmConfig(false))
    .build()
    
MoEngage.initialiseDefaultInstance(moEngage)
```

###### Pass the push token to the MoEngage SDK

The application must pass the Push Token received from FCM to the MoEngage SDK so the MoEngage platform can send push notifications to the device.
You can use the below API to pass the push token to the MoEngage SDK.

```kotlin
MoEFireBaseHelper.getInstance().passPushToken(getApplicationContext(), token);
```

Please make sure the token is passed to the MoEngage SDK whenever the push token is refreshed and on application updates. Passing the token on application updates is important for migration to the MoEngage Platform.

###### Passing the Push payload to the MoEngage SDK

To pass the push payload to the MoEngage SDK, call the MoEngage API from the Firebase receiver's `onMessageReceived()`.
Before passing the payload to the MoEngage SDK, you should check if it is from the MoEngage platform using the helper API provided by the SDK.

```kotlin
if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.getData())) {
  MoEFireBaseHelper.getInstance().passPushPayload(getApplicationContext(), remoteMessage.getData());
} else {
  // your app's business logic to show notification
}
```

##### Push Registration and Receiving handled by SDK

Add the below code to your manifest file.

```xml
<service android:name="com.moengage.firebase.MoEFireBaseMessagingService">
 	<intent-filter>
		<action android:name="com.google.firebase.MESSAGING_EVENT" />
 	</intent-filter>
</service>
```

When the MoEngage SDK handles push registration, it optionally provides a callback to the Application whenever a new token is registered or the token is refreshed.

An application can get this callback by implementing `FirebaseEventListener` and registering for a callback in the Application class `onCreate()` using `MoEFireBaseHelper.getInstance().addEventListener()`


Refer to the [MoEngage - API reference](https://moengage.github.io/android-api-reference-v11/moe-push-firebase/com.moengage.firebase.listener/-firebase-event-listener/index.html) for more details on the listener.


##### Callbacks

We recommend adding the callbacks in the onCreate() of the Application class since they can be triggered even when the application is in the background.

###### Token Callback

When MoEngage SDK handles push registration, it optionally provides a callback to the application whenever a new token is registered or the token is refreshed. To get the token callback, implement the [TokenAvailableListener](https://moengage.github.io/android-api-reference/pushbase/com.moengage.pushbase.listener/-token-available-listener/index.html) and register for the callback using [MoEFireBaseHelper.getInstance().addTokenListener()](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase/-mo-e-fire-base-helper/add-token-listener.html).

###### Non-MoEngage Payload

If you're using the receiver provided by the SDK in your application's manifest file, SDK provides a callback in case a push payload is received for any other server apart from MoEngage Platform. To get a callback implement the [NonMoEngagePushListener](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase.listener/-non-mo-engage-push-listener/index.html) and register for the callback using [MoEFireBaseHelper.getInstance().addNonMoEngagePushListener()](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase/-mo-e-fire-base-helper/add-non-mo-engage-push-listener.html).


You are now all set up to receive push notifications from MoEngage. For more information on features provided in the MoEngage Android SDK, refer to the following links:

* [Push Notifications](https://developers.moengage.com/hc/en-us/sections/360013606771-Push)

* [Location Triggered](https://developers.moengage.com/hc/en-us/articles/4403443036564-Location-Triggered)

* [In-App messaging](https://developers.moengage.com/hc/en-us/sections/360013831431-In-App-Messages)

* [Notification Center](https://developers.moengage.com/hc/en-us/articles/4403878923284-Notification-Center)

* [API Reference](https://moengage.github.io/android-api-reference/index.html)

* [Compliance](https://developers.moengage.com/hc/en-us/sections/4403894212116-Compliance)

* [Release Notes](https://developers.moengage.com/hc/en-us/articles/4403896795540-Changelog)

### Identify
Use [identify](https://segment.com/docs/connections/sources/catalog/libraries/mobile/kotlin-android/#identify) to track user-specific attributes. It is equivalent to tracking user attributes on MoEngage. MoEngage supports traits supported by Segment as well as custom traits. If you set traits.id, we set that as the Unique ID for that user.

### Track
Use [track](https://segment.com/docs/connections/sources/catalog/libraries/mobile/kotlin-android/#track) to track events and user behavior in your app.
This will send the event to MoEngage with the associated properties. Tracking events is essential and will help you create segments for engaging users.

### Reset
Reset
If your app supports the ability for a user to log out and log in with a new identity, then you’ll need to call [reset](https://segment.com/docs/connections/sources/catalog/libraries/mobile/kotlin-android/#reset) for the Analytics client.

### Example App

To build and run the `kotlin-example` application, you need to add your `write_key` from the Segment Dashboard and MoEngage Workspace Id to the `local.properties` file

```
segmentWriteKey=[your_write_key]
moengageWorkspaceId=[YOUR_MOENGAGE_WORKSPACE_ID]
```

## License

```
WWWWWW||WWWWWW
 W W W||W W W
      ||
    ( OO )__________
     /  |           \
    /o o|    MIT     \
    \___/||_||__||_|| *
         || ||  || ||
        _||_|| _||_||
       (__|__|(__|__|

The MIT License (MIT)

Copyright (c) 2014 Segment, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
