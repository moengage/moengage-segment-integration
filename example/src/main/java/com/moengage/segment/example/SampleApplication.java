package com.moengage.segment.example;

import android.app.Application;
import com.moengage.core.Logger;
import com.moengage.core.MoEngage;
import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.moengage.MoEngageIntegration;

/**
 * @author Umang Chamaria
 * Date: 2020/09/02
 */
public class SampleApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();

    Analytics analytics = new Analytics.Builder(this, "write_key")
        .use(MoEngageIntegration.FACTORY)
        .build();
    Analytics.setSingletonInstance(analytics);

    MoEngage moEngage = new MoEngage.Builder(this, "XXXXXXXX")
        .setLogLevel(Logger.VERBOSE)
        .enableSegmentIntegration()
        .build();

    MoEngage.initialise(moEngage);
  }
}
