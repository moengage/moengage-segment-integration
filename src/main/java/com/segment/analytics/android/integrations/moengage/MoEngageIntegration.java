package com.segment.analytics.android.integrations.moengage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.moe.pushlibrary.MoEHelper;
import com.moe.pushlibrary.PayloadBuilder;
import com.moe.pushlibrary.models.GeoLocation;
import com.moe.pushlibrary.utils.MoEHelperConstants;
import com.moengage.core.ConfigurationProvider;
import com.moengage.core.Logger;
import com.segment.analytics.Analytics;
import com.segment.analytics.AnalyticsContext;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.TrackPayload;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
 * @see <a href="http://docs.moengage.com/en/latest/android.html">MoEngage Android SDK</a>
 */
public class MoEngageIntegration extends Integration<MoEHelper> {
  public static final Factory FACTORY = new Factory() {
    @Override public Integration<?> create(ValueMap settings, Analytics analytics) {
      return new MoEngageIntegration(analytics, settings);
    }

    @Override public String key() {
      return KEY_MOENGAGE;
    }
  };
  private static final String KEY_MOENGAGE = "MoEngage";
  private static final Map<String, String> MAPPER;

  private static final String TAG = "MoEngageIntegration";

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
  private ConfigurationProvider provider;
  private boolean isAnonymousIdTracked;

  MoEngageIntegration(Analytics analytics, ValueMap settings) throws IllegalStateException {
    Context context = analytics.getApplication();
    String apiKey = settings.getString("apiKey");
    String pushSenderId = settings.getString("pushSenderId");
    helper = MoEHelper.getInstance(context);
    Logger.d("MoEngageIntegration : Segment MoEngage Integration initialized");
    helper.initialize(pushSenderId, apiKey);
    provider = ConfigurationProvider.getInstance(context);
    provider.setSegmentEnabledFlag(true);
    trackAnonymousId(analytics);
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    super.onActivityCreated(activity, savedInstanceState);
    if (helper == null && activity != null) {
      helper = MoEHelper.getInstance(activity.getApplicationContext());
    }
    if (savedInstanceState != null) {
      if (helper != null) helper.onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override public void onActivityStarted(Activity activity) {
    super.onActivityStarted(activity);
    if (helper != null && activity != null) helper.onStart(activity);
  }


  @Override public void onActivityResumed(Activity activity) {
    super.onActivityResumed(activity);
    if (helper != null && activity != null) helper.onResume(activity);
  }

  @Override public void onActivityPaused(Activity activity) {
    super.onActivityPaused(activity);
  }

  @Override public void onActivityStopped(Activity activity) {
    super.onActivityStopped(activity);
    if (helper != null && activity != null) helper.onStop(activity);
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    super.onActivitySaveInstanceState(activity, outState);
    if (helper != null) helper.onSaveInstanceState(outState);
  }

  @Override public void identify(IdentifyPayload identify) {
    super.identify(identify);
    Traits traits = identify.traits();

    if (!isNullOrEmpty(traits)) {
      transformUserAttributeToMoEngageFormat(transform(traits, MAPPER));
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
    if (!isAnonymousIdTracked) saveAnonymousId(identify.anonymousId());
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    if (!isNullOrEmpty(track)) {
      if (!isNullOrEmpty(track.properties())) {
        helper.trackEvent(track.event(), transformEventAttributesToMoEngageFormat(track.properties()
            .toJsonObject()));
      } else {
        helper.trackEvent(track.event());
      }
    }
    if (!isAnonymousIdTracked) saveAnonymousId(track.anonymousId());
  }

  @Override public void reset() {
    super.reset();
    helper.logoutUser();
    isAnonymousIdTracked = false;
  }

  @Override public MoEHelper getUnderlyingInstance() {
    return helper;
  }

  /**
   * Converts the {@link Properties} to a format understood by MoEngage SDK.
   *
   * @param eventAttributes Properties passed by the Segment SDK.
   * @return {@link JSONObject} of properties converted to MoEngage Format.
   */
  private JSONObject transformEventAttributesToMoEngageFormat(JSONObject eventAttributes){
    try{
      Logger.v(TAG + " transformEventAttributesToMoEngageFormat() : Transforming track properties "
          + " to MoEngage format");
      PayloadBuilder builder = new PayloadBuilder();
      Iterator iterator = eventAttributes.keys();
      while (iterator.hasNext()) {
        String key = (String) iterator.next();
        Object value = eventAttributes.get(key);
        if (value instanceof String){
          if (!isDate((String)value)){
            builder.putAttrObject(key, value);
          }else {
            builder.putAttrISO8601Date(key, (String) value);
          }
        }else {
          builder.putAttrObject(key, value);
        }
      }
      return builder.build();
    }catch (Exception e){
      Logger.f( TAG + " transformEventAttributesToMoEngageFormat() : ");
      return eventAttributes;
    }
  }

  /**
   * Transforms and tracks Identifiers in MoEngage format.
   *
   * @param userAttributes {@link Map} of identifiers passed by the Segment SDK.
   */
  private void transformUserAttributeToMoEngageFormat(Map<String, Object>
      userAttributes){
    try {
      Logger.v(TAG + " transformUserAttributeToMoEngageFormat() : Transforming identifiers to "
          + "MoEngage format.");
      List<String> removeAttributeList = new ArrayList<>();
      for (Map.Entry<String, Object> entry: userAttributes.entrySet()){
        String attributeName = entry.getKey();
        Object attributeValue = entry.getValue();
        if (attributeValue instanceof String && isDate((String) attributeValue)){
          helper.setUserAttributeISODate(attributeName, attributeValue.toString());
          removeAttributeList.add(attributeName);
        }
      }
      for (String attribute: removeAttributeList){
        userAttributes.remove(attribute);
      }
    } catch (Exception e) {
      Logger.f( TAG + " transformUserAttributeToMoEngageFormat() : Exception ", e);
    }
    helper.setUserAttribute(userAttributes);
  }

  private boolean isDate(String attributeString){
    try {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
      long epoch = format.parse(attributeString).getTime();
      return epoch > -1;
    }catch (Exception e){
      Logger.e( TAG + " isDate() : Exception: ", e);
    }
    try{
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.ENGLISH);
      long epoch = format.parse(attributeString).getTime();
      return epoch > -1;
    }catch (Exception e){
      Logger.e( TAG + " isDate() : Exception: ", e);
    }
    return false;
  }

  private void trackAnonymousId(Analytics analytics) {
    try {
      if (analytics.getAnalyticsContext() != null
          && analytics.getAnalyticsContext().traits() != null) {
        saveAnonymousId(analytics.getAnalyticsContext().traits().anonymousId());
      }else {
        Logger.e( TAG + " trackAnonymousId() : Traits object not found. Cannot track anonymous id");
      }
    } catch (Throwable e) {
      Logger.f(TAG + " trackAnonymousId() : Exception: ", e);
    }
  }

  private void saveAnonymousId(String anonymousId) {
    try {
      if (!TextUtils.isEmpty(anonymousId)) {
        provider.saveSegmentAnonymousId(anonymousId);
        isAnonymousIdTracked = true;
      }
    } catch (Exception e) {
      Logger.f(TAG + " saveAnonymousId() : Exception: ", e);
    }
  }
}
