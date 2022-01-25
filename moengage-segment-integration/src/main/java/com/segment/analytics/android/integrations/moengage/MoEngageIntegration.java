package com.segment.analytics.android.integrations.moengage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.moengage.core.LogLevel;
import com.moengage.core.MoECoreHelper;
import com.moengage.core.analytics.MoEAnalyticsHelper;
import com.moengage.core.internal.CoreConstants;
import com.moengage.core.internal.integrations.MoEIntegrationHelper;
import com.moengage.core.internal.logger.Logger;
import com.moengage.core.internal.model.IntegrationMeta;
import com.moengage.core.model.GeoLocation;
import com.moengage.core.model.IntegrationPartner;
import com.segment.analytics.Analytics;
import com.segment.analytics.AnalyticsContext;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.TrackPayload;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;

import static com.segment.analytics.internal.Utils.isNullOrEmpty;
import static com.segment.analytics.internal.Utils.transform;

/**
 * MoEngage is an advanced mobile marketing and engagement tool which has a wide range of
 * features. It helps user retention and increases churn.
 *
 * @see <a href="http://www.moengage.com/">MoEngage</a>
 * @see <a href="https://segment.com/docs/integrations/moengage/">MoEngage Integration</a>
 * @see <a href="https://docs.moengage.com/docs/android-integration">MoEngage Android SDK</a>
 */
public class MoEngageIntegration extends Integration<MoEAnalyticsHelper> {

  public static final Factory FACTORY = new Factory() {
    @Override public Integration<?> create(ValueMap settings, Analytics analytics) {
      return new MoEngageIntegration(analytics, settings);
    }

    @Override @NonNull public String key() {
      return KEY_MOENGAGE;
    }
  };

  private static final String KEY_MOENGAGE = "MoEngage";
  private static final Map<String, String> MAPPER;

  private static final String TAG =
      "MoEngageIntegration_" + BuildConfig.MOENGAGE_SEGMENT_SDK_VERSION;

  static {
    Map<String, String> mapper = new LinkedHashMap<>();
    mapper.put("anonymousId", "USER_ATTRIBUTE_SEGMENT_ID");
    mapper.put("email", CoreConstants.USER_ATTRIBUTE_USER_EMAIL);
    mapper.put("userId", CoreConstants.USER_ATTRIBUTE_UNIQUE_ID);
    mapper.put("name", CoreConstants.USER_ATTRIBUTE_USER_NAME);
    mapper.put("phone", CoreConstants.USER_ATTRIBUTE_USER_MOBILE);
    mapper.put("firstName", CoreConstants.USER_ATTRIBUTE_USER_FIRST_NAME);
    mapper.put("lastName", CoreConstants.USER_ATTRIBUTE_USER_LAST_NAME);
    mapper.put("gender", CoreConstants.USER_ATTRIBUTE_USER_GENDER);
    mapper.put("birthday", CoreConstants.USER_ATTRIBUTE_USER_BDAY);
    MAPPER = Collections.unmodifiableMap(mapper);
  }

  MoEAnalyticsHelper helper;
  private final MoEIntegrationHelper integrationHelper;
  private final Context context;
  private final String instanceId;

  MoEngageIntegration(Analytics analytics, ValueMap settings) throws IllegalStateException {
    context = analytics.getApplication();
    instanceId = settings.getString("apiKey");
    helper = MoEAnalyticsHelper.INSTANCE;
    integrationHelper = new MoEIntegrationHelper(context, IntegrationPartner.SEGMENT);
    Logger.print(() -> TAG + " Segment Integration initialised.");
    integrationHelper.initialize(instanceId, analytics.getApplication());
    MoEIntegrationHelper.Companion.addIntegrationMeta(
        new IntegrationMeta("", BuildConfig.MOENGAGE_SEGMENT_SDK_VERSION), instanceId);
    trackAnonymousId(analytics);
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    try {
      super.onActivityCreated(activity, savedInstanceState);
      Logger.print(() -> TAG + " onActivityCreated(): ");
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " onActivityCreated(): ");
    }
  }

  @Override public void onActivityStarted(Activity activity) {
    try {
      super.onActivityStarted(activity);
      Logger.print(() -> TAG + " onActivityStarted(): ");
      integrationHelper.onActivityStart(activity);
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " onActivityStarted(): ");
    }
  }

  @Override public void onActivityResumed(Activity activity) {
    try {
      super.onActivityResumed(activity);
      Logger.print(() -> TAG + " onActivityResumed(): ");
        integrationHelper.onActivityResumed(activity);
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " onActivityResumed() ");
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
  }

  @Override public void onActivityStopped(Activity activity) {
    try {
      super.onActivityStopped(activity);
      Logger.print(() -> TAG + " onActivityStopped(): ");
      integrationHelper.onActivityStop(activity);
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " onActivityStopped(): ");
    }
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    try {
      super.onActivitySaveInstanceState(activity, outState);
      Logger.print(() -> TAG + " onActivitySaveInstanceState(): ");
      integrationHelper.onActivitySavedInstance(activity, outState);
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " onActivitySaveInstanceState(): ");
    }
  }

  @Override public void identify(IdentifyPayload identify) {
    try {
      super.identify(identify);
      Logger.print(() -> TAG + " identify(): ");
      Traits traits = identify.traits();

      if (!isNullOrEmpty(traits)) {
        integrationHelper.trackUserAttribute(transform(traits, MAPPER), instanceId);
        Traits.Address address = traits.address();
        if (!isNullOrEmpty(address)) {
          String city = address.city();
          if (!isNullOrEmpty(city)) {
            helper.setUserAttribute(context, "city", city, instanceId);
          }
          String country = address.country();
          if (!isNullOrEmpty(country)) {
            helper.setUserAttribute(context, "country", country, instanceId);
          }
          String state = address.state();
          if (!isNullOrEmpty(state)) {
            helper.setUserAttribute(context, "state", state, instanceId);
          }
        }
      }

      AnalyticsContext.Location location = identify.context().location();
      if (!isNullOrEmpty(location)) {
        helper.setUserAttribute(context, CoreConstants.USER_ATTRIBUTE_USER_LOCATION,
            new GeoLocation(location.latitude(), location.longitude()), instanceId);
      }
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " identify(): ");
    }
  }

  @Override public void track(TrackPayload track) {
    try {
      super.track(track);
      Logger.print(() -> TAG + " track(): ");
      if (!isNullOrEmpty(track)) {
        if (!isNullOrEmpty(track.properties())) {
          integrationHelper.trackEvent(track.event(), track.properties().toJsonObject(),
              instanceId);
        } else {
          integrationHelper.trackEvent(track.event(), new JSONObject(), instanceId);
        }
      }
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " track(): ");
    }
  }

  @Override public void reset() {
    try {
      super.reset();
      Logger.print(() -> TAG + " reset(): ");
      MoECoreHelper.INSTANCE.logoutUser(context, instanceId);
    } catch (Exception e) {
      Logger.print(LogLevel.ERROR, e, () -> TAG + " reset(): ");
    }
  }

  @Override public MoEAnalyticsHelper getUnderlyingInstance() {
    return helper;
  }

  private void trackAnonymousId(Analytics analytics) {
    try {
      integrationHelper.trackAnonymousId(analytics.getAnalyticsContext().traits().anonymousId(),
          instanceId);
    } catch (Throwable th) {
      Logger.print(LogLevel.ERROR, th, () -> TAG + " trackAnonymousId(): ");
    }
  }
}
