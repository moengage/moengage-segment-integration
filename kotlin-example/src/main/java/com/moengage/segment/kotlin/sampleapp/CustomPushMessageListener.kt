/*
 * Copyright (c) 2014-2025 MoEngage Inc.
 *
 * All rights reserved.
 *
 *  Use of source code or binaries contained within MoEngage SDK is permitted only to enable use of the MoEngage platform by customers of MoEngage.
 *  Modification of source code and inclusion in mobile apps is explicitly allowed provided that all other conditions are met.
 *  Neither the name of MoEngage nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *  Redistribution of source code or binaries is disallowed except with specific prior written permission. Any such redistribution must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.moengage.segment.kotlin.sampleapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.moengage.pushbase.model.NotificationPayload
import com.moengage.pushbase.push.PushMessageListener

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
    ): NotificationCompat.Builder? {
        // get the object constructed by MoEngage SDK
        val builder = super.onCreateNotification(context, notificationPayload)
        // customise as required.
        // below customisation is only for illustration purpose. You can chose to have other
        // customisations as required by the application.
        builder?.setOngoing(true)
        // return the builder object to the SDK for posting notification.
        return builder
    }

    override fun onNotificationCleared(context: Context, payload: Bundle) {
        super.onNotificationCleared(context, payload)
        // callback for notification cleared.
    }

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
        // callback for push notification received.
    }

    override fun onNotificationClick(activity: Activity, payload: Bundle): Boolean {
        // callback for notification clicked. if you want to handle redirection then return true
        return false
    }
}
