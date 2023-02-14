package com.moengage.segment.example;

import android.content.Context;
import android.content.Intent;
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
    setTitle("MoEngage-Segment : Sample");
    findViewById(R.id.button_analytics_android).setOnClickListener(v -> {
      startActivity(new Intent(this, AnalyticsAndroidActivity.class));
    });
    findViewById(R.id.button_analytics_kotlin).setOnClickListener(v -> {
      startActivity(new Intent(this, AnalyticsKotlinActivity.class));
    });
  }
}
