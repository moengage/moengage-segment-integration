package com.segment.analytics.android.integrations.moengage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.models.GeoLocation;
import com.moe.pushlibrary.utils.MoEHelperConstants;
import com.moengage.core.Logger;
import com.moengage.core.MoEConstants;
import com.moengage.core.MoEIntegrationHelper;
import com.moengage.core.model.IntegrationMeta;
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
public class MoEngageIntegration extends Integration<MoEHelper> {
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
    mapper.put("email", MoEHelperConstants.USER_ATTRIBUTE_USER_EMAIL);
    mapper.put("userId", MoEHelperConstants.USER_ATTRIBUTE_UNIQUE_ID);
    mapper.put("name", MoEHelperConstants.USER_ATTRIBUTE_USER_NAME);
    mapper.put("phone", MoEHelperConstants.USER_ATTRIBUTE_USER_MOBILE);
    mapper.put("firstName", MoEHelperConstants.USER_ATTRIBUTE_USER_FIRST_NAME);
    mapper.put("lastName", MoEHelperConstants.USER_ATTRIBUTE_USER_LAST_NAME);
    mapper.put("gender", MoEHelperConstants.USER_ATTRIBUTE_USER_GENDER);
    mapper.put("birthday", MoEHelperConstants.USER_ATTRIBUTE_USER_BDAY);
    MAPPER = Collections.unmodifiableMap(mapper);
  }

  MoEHelper helper;
  private MoEIntegrationHelper integrationHelper;

  MoEngageIntegration(Analytics analytics, ValueMap settings) throws IllegalStateException {
    Context context = analytics.getApplication();
    String apiKey = settings.getString("apiKey");
    String pushSenderId = settings.getString("pushSenderId");
    helper = MoEHelper.getInstance(context);
    integrationHelper = new MoEIntegrationHelper(context, IntegrationPartner.SEGMENT);
    Logger.d(TAG + " Segment MoEngage Integration initialized");
    integrationHelper.initialize(apiKey, pushSenderId);
    MoEIntegrationHelper.setIntegrationMeta(new IntegrationMeta(
        MoEConstants.INTEGRATION_TYPE_SEGMENT, BuildConfig.MOENGAGE_SEGMENT_SDK_VERSION));
    trackAnonymousId(analytics);
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    try {
      super.onActivityCreated(activity, savedInstanceState);
      Logger.v(TAG + " onActivityCreated() : ");
      if (helper == null && activity != null) {
        helper = MoEHelper.getInstance(activity.getApplicationContext());
      }
    } catch (Exception e) {
      Logger.e(TAG + "onActivityCreated() : ", e);
    }
  }

  @Override public void onActivityStarted(Activity activity) {
    try {
      super.onActivityStarted(activity);
      Logger.v(TAG + " onActivityStarted() : ");
      if (helper != null && activity != null) integrationHelper.onActivityStart(activity);
    } catch (Exception e) {
      Logger.e(TAG + "onActivityStarted() : ", e);
    }
  }


  @Override public void onActivityResumed(Activity activity) {
    try {
      super.onActivityResumed(activity);
      Logger.v(TAG + " onActivityResumed() : ");
      if (integrationHelper != null && activity != null) integrationHelper.onActivityResumed(activity);
    } catch (Exception e) {
      Logger.e(TAG + "onActivityResumed() : ", e);
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
  }

  @Override public void onActivityStopped(Activity activity) {
    try {
      super.onActivityStopped(activity);
      Logger.v(TAG + " onActivityStopped() : ");
      if (integrationHelper != null && activity != null) integrationHelper.onActivityStop(activity);
    } catch (Exception e) {
      Logger.e(TAG + "onActivityStopped() : ", e);
    }
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    try {
      super.onActivitySaveInstanceState(activity, outState);
      Logger.v(TAG + " onActivitySaveInstanceState() : ");
      if (integrationHelper != null) integrationHelper.onActivitySavedInstance(activity, outState);
    } catch (Exception e) {
      Logger.e(TAG + "onActivitySaveInstanceState() : ", e);
    }
  }

  @Override public void identify(IdentifyPayload identify) {
    try {
      super.identify(identify);
      Logger.v(TAG + " identify() : ");
      Traits traits = identify.traits();

      if (!isNullOrEmpty(traits)) {
        integrationHelper.trackUserAttribute(transform(traits, MAPPER));
        Traits.Address address = traits.address();
        if (!isNullOrEmpty(address)) {
          String city = address.city();
          if (!isNullOrEmpty(city)) {
            helper.setUserAttribute("city", city);
          }
          String country = address.country();
          if (!isNullOrEmpty(country)) {
            helper.setUserAttribute("country", country);
          }
          String state = address.state();
          if (!isNullOrEmpty(state)) {
            helper.setUserAttribute("state", state);
          }
        }
      }

      AnalyticsContext.Location location = identify.context().location();
      if (!isNullOrEmpty(location)) {
        helper.setUserAttribute(MoEHelperConstants.USER_ATTRIBUTE_USER_LOCATION,
            new GeoLocation(location.latitude(), location.longitude()));
      }
    } catch (Exception e) {
      Logger.e(TAG + " identify() : ", e);
    }
  }

  @Override public void track(TrackPayload track) {
    try {
      super.track(track);
      Logger.v(TAG + " track() : ");
      if (!isNullOrEmpty(track)) {
        if (!isNullOrEmpty(track.properties())) {
          integrationHelper.trackEvent(track.event(), track.properties().toJsonObject());
        } else {
          integrationHelper.trackEvent(track.event(), new JSONObject());
        }
      }
    } catch (Exception e) {
      Logger.e(TAG + " track() : ", e);
    }
  }

  @Override public void reset() {
    try {
      super.reset();
      Logger.v(TAG + " reset() : ");
      helper.logoutUser();
    } catch (Exception e) {
      Logger.e(TAG + " reset() : ", e);
    }
  }

  @Override public MoEHelper getUnderlyingInstance() {
    return helper;
  }

  private void trackAnonymousId(Analytics analytics){
    try{
     integrationHelper.trackAnonymousId(analytics.getAnalyticsContext().traits().anonymousId());
    }catch(Throwable th){
      Logger.e(TAG + " trackAnonymousId() : ", th);
    }
  }
}
