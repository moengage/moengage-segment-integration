package com.moengage.segment.example;

import android.app.Application;
import com.moengage.core.Logger;
import com.moengage.core.MoEngage;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.inapp.MoEInAppHelper;
import com.moengage.pushbase.MoEPushHelper;
import com.moengage.segment.example.callbacks.FcmEventListener;
import com.moengage.segment.example.callbacks.InAppCallback;
import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.moengage.MoEngageIntegration;

/**
 * @author Umang Chamaria
 * Date: 2020/09/02
 */
public class SampleApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();

    Analytics analytics = new Analytics.Builder(this, BuildConfig.SEGMENT_WRITE_KEY)
        .use(MoEngageIntegration.FACTORY)
        .build();
    Analytics.setSingletonInstance(analytics);

    MoEngage moEngage =
        new MoEngage.Builder(this, "")//enter your own app id
            .setLogLevel(Logger.VERBOSE)//enabling Logs for debugging
            .enableLogsForSignedBuild() //Make sure this is removed before apps are pushed to
            // production
            .setNotificationSmallIcon(
                R.drawable.icon)//small icon should be flat, pictured face on, and must be white
            // on a transparent background.
            .setNotificationLargeIcon(R.drawable.ic_launcher)
            .enableLocationServices()//enabled To track location and run geo-fence campaigns
            .enableMultipleNotificationInDrawer()// shows multiple notifications in drawer at one go
            .enableSegmentIntegration()
            .build();
    // initialize MoEngage
    MoEngage.initialise(moEngage);

    // FCM event listener
    MoEFireBaseHelper.Companion.getInstance().setEventListener(new FcmEventListener());

    // Setting CustomPushMessageListener for notification customisation
    MoEPushHelper.getInstance().setMessageListener(new CustomPushMessageListener());

    // register in-app listener
    MoEInAppHelper.getInstance().registerListener(new InAppCallback());
  }
}
