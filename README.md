![Logo](/.github/logo.png)

analytics-kotlin-destination-moengage
======================================
To use MoEngage in an Android app, you must perform the following steps to set up your environment.

![MavenBadge](https://maven-badges.herokuapp.com/maven-central/com.moengage/moengage-segment-kotlin-destination/badge.svg)

> info ""
> **Note:** `analytics-android-integration-moengage` will no longer be receiving updates. We recommend you to use `analytics-kotlin-destination-moengage`. For more details refer [moengage-segment-android-integration](https://partners.moengage.com/hc/en-us/articles/4409143474964-Android-device-mode-)

To enable the full functionality of MoEngage (like Push Notifications, InApp Messaging), complete the following steps in your Android app.

### Adding the MoEngage Dependency

Along with the Segment dependency, add the below dependency in your `app/build.gradle` file.

```groovy
   implementation("com.moengage:moengage-segment-kotlin-destination:$sdkVersion"   
```
replace `$sdkVersion` with the appropriate SDK version

The MoEngage SDK depends on the below Jetpack libraries provided by Google for its functioning, make you add them if not
 done already.

```groovy
    implementation("androidx.core:core:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.lifecycle:lifecycle-process:2.4.0")
```
Refer to the [SDK Configuration](https://developers.moengage.com/hc/en-us/articles/4401984733972-Android-SDK-Configuration) documentation to know more about the build config and other libraries used by the SDK.

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

Copy the APP ID from the Settings Page `Dashboard --> Settings --> App --> General` and initialize the MoEngage SDK in the `onCreate()` method of the `Application` class

> info ""
> **Note:** MoEngage recommend that you initialize the SDK on the main thread inside `onCreate()` and not create a worker thread and initialize the SDK on that thread.

```kotlin
// this is the instance of the application class and "XXXXXXXXXXX" is the APP ID from the dashboard.
val moEngage = MoEngage.Builder(this, "XXXXXXXXXXX")
       .enablePartnerIntegration(IntegrationPartner.SEGMENT)
       .build()
MoEngage.initialiseDefaultInstance(moEngage)
```
### Exclude MoEngage Storage File from Auto-Backup
Auto backup service of Andriod periodically backs up the Shared Preference file, Database files, and so on.

For more information, refer to [Auto Backup](https://developer.android.com/guide/topics/data/autobackup).

As a result of the backup, MoEngage SDK identifiers are backed up and restored after re-install.
The restoration of the identifier results in your data being corrupted and the user not being reachable using push notifications.

To ensure data isn't corrupted after a backup is restored, opt-out of MoEngage SDK storage files.

Refer to the [documentation](https://developers.moengage.com/hc/en-us/articles/4401999257236-Exclude-MoEngage-Storage-File-from-Auto-Backup) for further details.

### Install or update differentiation
This is required for migrations to the MoEngage Platform so the SDK can determine whether the user is a new user on your app, or an existing user who updated to the latest version.

If the user was already using your application and has just updated to a new version which has the MoEngage SDK, below is an example call:

```kotlin
 MoEAnalyticsHelper.setAppStatus(context, AppStatus.UPDATE);
```

If this is a fresh install:

```kotlin
MoEAnalyticsHelper.setAppStatus(context, AppStatus.INSTALL);
```

### Configure Push Notifications

Copy the Server Key from the FCM console and add it to the MoEngage Dashboard. To upload it, go to the Settings Page `Dashboard --> Settings --> Channel --> Push --> Mobile Push --> Android` and add the Server Key and package name.
**Please make sure you add the keys both in Test and Live environment.**

#### Add meta information for push notification

To display push notifications, some metadata regarding the notification is required. For example, the small icon and large icon drawables are mandatory.


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

For showing Push notifications there are 2 important steps:

1. Registration for Push, for example generating push token.
2. Receiving the Push payload from Firebase Cloud Messaging(FCM) service and showing the notification on the device.

##### Push registration and receiving handled by the application

###### Opt-out of MoEngage registration

To opt-out of MoEngage token registration mechanism disable token registration while configuring FCM in the `MoEngage.Builder` as shown below

```kotlin
val moEngage = MoEngage.Builder(this, "XXXXXXXX")
    .enablePartnerIntegration(IntegrationPartner.SEGMENT)
    .configureNotificationMetaData(NotificationConfig(R.drawable.small_icon, R.drawable.large_icon, R.color.notiColor, null, true, isBuildingBackStackEnabled = false, isLargeIconDisplayEnabled = true))
    .configureFcm(FcmConfig(false))
    .build()
    
MoEngage.initialiseDefaultInstance(moEngage)
```

###### Pass the push token to the MoEngage SDK

The Application must pass the Push Token received from FCM to the MoEngage SDK for the MoEngage platform to send out push notifications to the device.
Use the below API to pass the push token to the MoEngage SDK.

```kotlin
MoEFireBaseHelper.getInstance().passPushToken(getApplicationContext(), token);
```

Please make sure token is passed to MoEngage SDK whenever push token is refreshed and on application update. Passing token on application update is important for migration to the MoEngage Platform.

###### Passing the Push payload to the MoEngage SDK

To pass the push payload to the MoEngage SDK call the MoEngage API from the `onMessageReceived()` from the Firebase receiver.
Before passing the payload to the MoEngage SDK you should check if the payload is from the MoEngage platform using the helper API provided by the SDK.

```kotlin
if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.getData())) {
  MoEFireBaseHelper.getInstance().passPushPayload(getApplicationContext(), remoteMessage.getData());
} else {
  // your app's business logic to show notification
}
```

##### Push Registration and Receiving handled by SDK

Add the below code in your manifest file.

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

We recommend you to add the callbacks in the onCreate() of the Application class since these callbacks can be triggered even when the application is in the background.

###### Token Callback

When MoEngage SDK handles push registration, it optionally provides a callback to the application whenever a new token is registered or the token is refreshed. To get the token callback implement the [TokenAvailableListener](https://moengage.github.io/android-api-reference/pushbase/com.moengage.pushbase.listener/-token-available-listener/index.html) and register for the callback using [MoEFireBaseHelper.getInstance().addTokenListener()](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase/-mo-e-fire-base-helper/add-token-listener.html).

###### Non-MoEngage Payload

If you're using the receiver provided by the SDK in your application's manifest file, SDK provides a callback in case a push payload is received for any other server apart from MoEngage Platform. To get a callback implement the [NonMoEngagePushListener](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase.listener/-non-mo-engage-push-listener/index.html) and register for the callback using [MoEFireBaseHelper.getInstance().addNonMoEngagePushListener()](https://moengage.github.io/android-api-reference/moe-push-firebase/com.moengage.firebase/-mo-e-fire-base-helper/add-non-mo-engage-push-listener.html).


#### Declare and configure Rich Landing Activity:

A rich landing page can be used to open a web URL inside the app via a push campaign.

The configuration below is only required if you want to add a parent activity to the Rich landing page. If not, you can move to the next section.
To use a rich landing page you need to add the below code in the AndroidManifest.xml

Add the following snippet and replace `[PARENT_ACTIVITY_NAME]` with the name of the parent
 activity; `[ACTIVITY_NAME]` with the activity name which should be the parent of the Rich Landing Page

```xml
<activity
  android:name="com.moe.pushlibrary.activities.MoEActivity"
  android:label="[ACTIVITY_NAME]"
  android:parentActivityName="[PARENT_ACTIVITY_NAME]" >
</activity>
```

You are now all set up to receive push notifications from MoEngage. For more information on features provided in MoEngage Android SDK refer to the following links:

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

To build and run the `kotlin-example` application you need to add your `write_key` from the Segment Dashboard and MoEngage App Id to the `local.properties` file

```
segmentWriteKey=[your_write_key]
moengageAppId=[YOUR_MOENGAGE_APP_ID]
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
