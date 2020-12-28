package com.moengage.segment.example;

import android.app.Application;
import com.moengage.core.LogLevel;
import com.moengage.core.MoEngage;
import com.moengage.core.config.GeofenceConfig;
import com.moengage.core.config.LogConfig;
import com.moengage.core.config.NotificationConfig;
import com.moengage.core.internal.logger.Logger;
import com.moengage.core.model.IntegrationPartner;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.inapp.MoEInAppHelper;
import com.moengage.pushbase.MoEPushHelper;
import com.moengage.sampleapp.BuildConfig;
import com.moengage.sampleapp.R;
import com.moengage.segment.example.callbacks.FcmEventListener;
import com.moengage.segment.example.callbacks.InAppCallback;
import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.moengage.MoEngageIntegration;

/**
 * @author Umang Chamaria
 * Date: 2020/09/02
 */
public class SampleApplication extends Application {

  private static final String TAG = "SampleApplication";

  @Override public void onCreate() {
    super.onCreate();
    Logger.v(TAG + " onCreate() : ");
    Analytics analytics = new Analytics.Builder(this, BuildConfig.SEGMENT_WRITE_KEY)
        .use(MoEngageIntegration.FACTORY)
        .build();
    Analytics.setSingletonInstance(analytics);

    MoEngage moEngage =
        new MoEngage.Builder(this, "")//enter your own app id
            //set notification data(small icon, large icon, notification color,
            // notification tone, show multiple notifications in drawer etc..)
            .configureNotificationMetaData(
                new NotificationConfig(R.drawable.icon, R.drawable.ic_launcher,
                    R.color.notificationColor, null, true, false, true))
            //enabled To track location and run geo-fence campaigns
            .configureGeofence(new GeofenceConfig(true, true))
            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
            //Configure logs
            .configureLogs(new LogConfig(LogLevel.VERBOSE, false))
            .build();
    // initialize MoEngage
    MoEngage.initialise(moEngage);

    // FCM event listener
    MoEFireBaseHelper.Companion.getInstance().addEventListener(new FcmEventListener());

    // Setting CustomPushMessageListener for notification customisation
    MoEPushHelper.getInstance().setMessageListener(new CustomPushMessageListener());

    // register in-app listener
    MoEInAppHelper.getInstance().registerListener(new InAppCallback());
  }
}
