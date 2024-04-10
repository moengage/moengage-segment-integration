package com.moengage.segment.kotlin.sampleapp

import android.app.Application
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.ktx.MoEngageBuilderKtx
import com.moengage.core.model.IntegrationPartner
import com.moengage.inapp.MoEInAppHelper
import com.moengage.pushbase.MoEPushHelper
import com.moengage.segment.kotlin.sampleapp.callback.inapp.InAppClickListener
import com.moengage.segment.kotlin.sampleapp.callback.inapp.InAppLifecycleCallback
import com.segment.analytics.kotlin.android.Analytics
import com.segment.analytics.kotlin.core.Analytics
import com.segment.analytics.kotlin.destinations.moengage.MoEngageDestination

class KotlinSampleApplication : Application() {

    companion object {
        lateinit var analytics: Analytics
    }

    override fun onCreate() {
        super.onCreate()
        //Initialization Analytics Kotlin Instance
        analytics = Analytics(BuildConfig.SEGMENT_WRITE_KEY, this)
        analytics.add(MoEngageDestination(this))
        //enter your account's app id

        val moEngage = MoEngageBuilderKtx(
            this, BuildConfig.MOENAGE_APP_ID,
            dataCenter = DataCenter.DATA_CENTER_1,
            notificationConfig = NotificationConfig(
                smallIcon = R.drawable.icon,
                largeIcon = R.drawable.ic_launcher,
                notificationColor = R.color.notificationColor,
                isMultipleNotificationInDrawerEnabled = true,
                isBuildingBackStackEnabled = false,
                isLargeIconDisplayEnabled = true
            ),
            integrationPartner = IntegrationPartner.SEGMENT,
            logConfig = LogConfig(LogLevel.VERBOSE, false)
        ).build()

        MoEngage.initialiseDefaultInstance(moEngage)

        // Setting CustomPushMessageListener for notification customisation
        MoEPushHelper.getInstance().registerMessageListener(CustomPushMessageListener())

        //in-app related callbacks
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(InAppLifecycleCallback())
        MoEInAppHelper.getInstance().setClickActionListener(InAppClickListener())
    }
}