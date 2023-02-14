package com.moengage.segment.example

import android.app.Application
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.model.IntegrationPartner
import com.moengage.inapp.MoEInAppHelper
import com.moengage.pushbase.MoEPushHelper
import com.moengage.segment.example.callbacks.inapp.InAppClickListener
import com.moengage.segment.example.callbacks.inapp.InAppLifecycleCallback
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.moengage.MoEngageIntegration
import com.segment.analytics.kotlin.destinations.moengage.MoEngageDestination

/**
 * @author Umang Chamaria
 * Date: 2020/09/02
 */
class SampleApplication : Application() {

    lateinit var analyticsKt: com.segment.analytics.kotlin.core.Analytics
    override fun onCreate() {
        super.onCreate()
        //Initialization Analytics Android Instance
        val analytics = Analytics.Builder(this, BuildConfig.SEGMENT_WRITE_KEY)
            .use(MoEngageIntegration.FACTORY)
            .build()
        Analytics.setSingletonInstance(analytics)

        //Initialization Analytics Kotlin Instance
        analyticsKt = com.segment.analytics.kotlin.android.Analytics(
            BuildConfig.SEGMENT_WRITE_KEY,
            applicationContext
        )
        analyticsKt.add(MoEngageDestination(this))
        //enter your account's app id
        val moEngage: MoEngage = MoEngage.Builder(this, BuildConfig.MOENAGE_APP_ID)
            //set notification data(small icon, large icon, notification color,
            // notification tone, show multiple notifications in drawer etc..)
            .configureNotificationMetaData(
                NotificationConfig(
                    smallIcon = R.drawable.icon,
                    largeIcon = R.drawable.ic_launcher,
                    notificationColor = R.color.notificationColor,
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = false,
                    isLargeIconDisplayEnabled = true
                )
            )
            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
            //Configure logs
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()
        // initialize MoEngage
        MoEngage.initialiseDefaultInstance(moEngage)

        // Setting CustomPushMessageListener for notification customisation
        MoEPushHelper.getInstance().registerMessageListener(CustomPushMessageListener())

        //in-app related callbacks
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(InAppLifecycleCallback())
        MoEInAppHelper.getInstance().setClickActionListener(InAppClickListener())
    }
}