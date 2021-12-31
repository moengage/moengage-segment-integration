package com.moengage.segment.example

import com.moengage.pushbase.push.PushMessageListener
import android.os.Bundle
import com.moengage.pushbase.model.NotificationPayload
import android.app.Activity
import android.content.Context
import androidx.core.app.NotificationCompat

class CustomPushMessageListener : PushMessageListener() {
    // decide whether notification should be shown or not. If super() returns false this method
    // should return false. In case super() isn't called notification will not be displayed.
    override fun isNotificationRequired(context: Context, payload: Bundle): Boolean {
        val shouldDisplayNotification = super.isNotificationRequired(context, payload)
        // do not show notification if MoEngage SDK returns false.
        if (shouldDisplayNotification) {
            // app's logic to decide whether to show notification or not.
            // for illustration purpose reading notification preference from SharedPreferences and
            // deciding whether to show notification or not. Logic can vary from application to
            // application.
            val preferences = context.getSharedPreferences("demoapp", 0)
            return preferences.getBoolean("notification_preference", true)
        }
        return shouldDisplayNotification
    }

    // customise the notification builder object as required
    override fun onCreateNotification(
        context: Context,
        notificationPayload: NotificationPayload
    ): NotificationCompat.Builder {
        // get the object constructed by MoEngage SDK
        val builder = super.onCreateNotification(context, notificationPayload)
        // customise as required.
        // below customisation is only for illustration purpose. You can chose to have other
        // customisations as required by the application.
        builder.setOngoing(true)
        // return the builder object to the SDK for posting notification.
        return builder
    }

    override fun onNotificationCleared(context: Context, payload: Bundle) {
        super.onNotificationCleared(context, payload)
        // callback for notification cleared.
    }

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
        //callback for push notification received.
    }

    override fun onHandleRedirection(activity: Activity, payload: Bundle) {
        super.onHandleRedirection(activity, payload)
        //callback for notification clicked. if you want to handle redirection then do not call super()
        // and add the redirection logic here.
    }
}