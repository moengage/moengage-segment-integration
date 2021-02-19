![Logo](/.github/logo.png)

analytics-android-integration-moengage
======================================

![MavenBadge](https://maven-badges.herokuapp.com/maven-central/com.moengage/moengage-segment-integration/badge.svg)

To enable its full functionality (like Push Notifications, InApp Messaging), there are still a couple of steps that you have to take care of in your Android app.

#### Adding MoEngage Dependency:

Along with the segment, dependency add the below dependency in your build.gradle file.

```groovy
 implementation("com.moengage:moengage-segment-integration:$sdkVersion") {
        transitive = true
    }
```
where `$sdkVersion` should be replaced by the latest version of the MoEngage SDK.

MoEngage SDK depends on the below Jetpack libraries provided by Google for its functioning, make sure you add them if it isn't already presnt in the application.
 
```groovy
    implementation("androidx.core:core:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.lifecycle:lifecycle-process:2.2.0")
```
Refer to the [SDK Configuration](https://docs.moengage.com/docs/android-sdk-configuration) documentation to know more about the build config and other libraries used by the SDK.
 
#### Register MoEngage with Segment SDK:

After adding the dependency, you must register the integration with Segment SDK. To do this, import the MoEngage
 integration:

```java
import com.segment.analytics.android.integrations.moengage.MoEngageIntegration;
```

Add the following line:

```java
Analytics analytics = new Analytics.Builder(this, "write_key")
                .use(MoEngageIntegration.FACTORY)
                .build();
```


#### How to Initialise MoEngage SDK: 
Get APP ID from the Settings Page `Dashboard --> Settings --> App --> General` and initialize the MoEngage SDK in the `Application` class's `onCreate()`
***Note: It is recommended that you initialize the SDK on the main thread inside `onCreate()` and not create a worker thread and initialize the SDK on that thread.*** 

```java
// this is the instance of the application class and "XXXXXXXXXXX" is the APP ID from the dashboard.
MoEngage moEngage = new MoEngage.Builder(this, "XXXXXXXXXXX")
            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
            .build();
MoEngage.initialise(moEngage);
```

#### Install/Update Differentiation
This is solely required for migration to the MoEngage Platform. We need your help to tell the SDK whether the user is
 a new user for your application, or an existing user who has updated to the latest version.

If the user was already using your application and has just updated to a new version which has MoEngage SDK it is an updated call the below API

```java
MoEHelper.getInstance(getApplicationContext()).setAppStatus(AppStatus.UPDATE);
```

In case it is a fresh install call the below API

```java
MoEHelper.getInstance(getApplicationContext()).setAppStatus(AppStatus.INSTALL);
```

#### How To - Push Notifications:
Copy the Server Key from the FCM console and add it to the MoEngage Dashboard(Not sure where to find the Server Key refer to [Getting FCM Server Key](https://docs.moengage.com/docs/getting-fcmgcm-server-key)). To upload it, go to the Settings Page `Dashboard --> Settings --> Channel --> Push --> Mobile Push --> Android` and add the Server Key and package name.
**Please make sure you add the keys both in Test and Live environment.**

##### Adding meta information for push notification

To show push notifications some metadata regarding the notification is required where the small icon and large icon drawables being the mandatory ones.
Refer to the [NotificationConfig](https://moengage.github.io/android-api-reference/core/core/com.moengage.core.config/-notification-config/index.html) API reference for all the possible options.

Use the `configureNotificationMetaData()` to pass on the configuration to the SDK.

```java
MoEngage moEngage =
        new MoEngage.Builder(this, "XXXXXXXXXX")
            .configureNotificationMetaData(new NotificationConfig(R.drawable.small_icon, R.drawable.large_icon, R.color.notiColor, "sound", true, true, true))
            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
            .build();
MoEngage.initialise(moEngage);
```

##### Configuring Firebase Cloud Messaging

For showing Push notifications there are 2 important things 

1. Registration for Push, i.e. generating push token.
2. Receiving the Push payload from Firebase Cloud Messaging(FCM) service and showing the notification on the device. 

##### Push Registration and Receiving handled by App

By default, MoEngage SDK attempts to register for push token, since your application is handling push you need to opt-out of SDK's token registration.

###### How to opt-out of MoEngage Registration?

To opt-out of MoEngage's token registration mechanism disable token registration while configuring FCM in the `MoEngage.Builder` as shown below

```java
MoEngage moEngage = new MoEngage.Builder(this, "XXXXXXXXXX")
            .configureNotificationMetaData(new NotificationConfig(R.drawable.small_icon, R.drawable.large_icon, R.color.notiColor, "sound", true, true, true))
            .configureFcm(FcmConfig(false))
            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
            .build();
MoEngage.initialise(moEngage);
```

###### Pass the Push Token To MoEngage SDK
The Application would need to pass the Push Token received from FCM to the MoEngage SDK for the MoEngage platform to send out push notifications to the device.
Use the below API to pass the push token to the MoEngage SDK.

```java
MoEFireBaseHelper.getInstance().passPushToken(getApplicationContext(), token);
```
*Note:* Please make sure token is passed to MoEngage SDK whenever push token is refreshed and on application update. Passing token on application update is important for migration to the MoEngage Platform.

###### Passing the Push payload to the MoEngage SDK
To pass the push payload to the MoEngage SDK call the MoEngage API from the `onMessageReceived()` from the Firebase receiver.
Before passing the payload to the MoEngage SDK you should check if the payload is from the MoEngage platform using the helper API provided by the SDK.

```java
if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.getData())) {
  MoEFireBaseHelper.Companion.getInstance().passPushPayload(getApplicationContext(), remoteMessage.getData());
}else{
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
When MoEngage SDK handles push registration it optionally provides a callback to the Application whenever a new token is registered or token is refreshed. 
An application can get this callback by implementing `FirebaseEventListener` and registering for a callback in the Application class' `onCreate()` using `MoEFireBaseHelper.getInstance().addEventListener()` 
Refer to the [API reference](https://moengage.github.io/android-api-reference/moe-push-firebase/moe-push-firebase/com.moengage.firebase.listener/-firebase-event-listener/index.html) for more details  on the listener.

##### 4. Declaring & configuring Rich Landing Activity: 
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

 * [Push Notifications](http://docs.moengage.com/docs/push-configuration)

 * [Geofence](https://docs.moengage.com/docs/android-geofence)
 
 * [In-App messaging](https://docs.moengage.com/docs/android-in-app-nativ)
 
 * [Notification Center](https://docs.moengage.com/docs/android-notification-center)
 
 * [Advanced Configuration](https://docs.moengage.com/docs/android-advanced-integration)
 
 * [API Reference](https://moengage.github.io/android-api-reference/-modules.html)
 
 * [Compliance](https://docs.moengage.com/docs/android-compliance)

 * [Release Notes](https://docs.moengage.com/docs/segment-android-releases)

#### Identify
Use [Identify](https://segment.com/docs/sources/mobile/android/#identify) to track user-specific attributes. It is equivalent to tracking [user attributes](http://docs.moengage.com/docs/identifying-user) on MoEngage. MoEngage supports traits supported by Segment as well as custom traits. If you set traits.id, we set that as the Unique ID for that user.

#### Track
Use [track](https://segment.com/docs/sources/mobile/android/#track) to track events and user behavior in your app.
This will send the event to MoEngage with the associated properties. Tracking events is essential and will help you create segments for engaging users.

#### Reset
If your app supports the ability for a user to logout and login with a new identity, then you’ll need to call reset for the Analytics client.

### Example App

To build and run the example application you need to add your `write_key` from the Segment Dashboard to the `local
.properties` file

```
segmentWriteKey=[your_write_key]
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
