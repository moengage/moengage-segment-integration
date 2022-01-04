package com.moengage.segment.example;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //track user attributes
    trackUserInfo(getBaseContext(), "25", "Male", "Bengaluru");
    //track event without attributes
    Analytics.with(MainActivity.this).track("only event");
    //track event with attributes
    Analytics.with(MainActivity.this)
        .track("Email button Click", new Properties().putValue("email", "opened"));
  }

  // Method to track User Attributes
  private void trackUserInfo(Context context, String age, String gender, String city) {
    //userId is mapped to USER_ATTRIBUTE_UNIQUE_ID of MoEngage SDK
    String userId = "mobiledev@moengage.com";
    String firstName = "Mobile";
    String lastName = "Dev";
    String fullName = firstName + " " + lastName;
    String email = "mobiledev@moengage.com";
    try {
      Traits userTraits = new Traits().putFirstName(firstName)
          .putLastName(lastName)
          .putName(fullName)
          .putEmail(email);
      if (age != null && !age.isEmpty()) {
        try {
          int parsedAge = Integer.parseInt(age);
          userTraits.putAge(parsedAge);
        } catch (NumberFormatException e) {
        }
      }
      if (gender != null && !gender.isEmpty()) {
        userTraits.putGender(gender);
      }
      if (city != null && !city.isEmpty()) {
        userTraits.put("user_location", city);
      }
      // Method provided by Segment for identification of users
      Analytics.with(context).identify(userId, userTraits, null);
    } catch (Exception e) {
    }
  }
}
