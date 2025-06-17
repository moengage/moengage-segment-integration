package com.moengage.segment.kotlin.sampleapp

import android.app.Application
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
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

        configureMoEngageSDK(this)
        configureSegmentSDK(this)
    }

    private fun configureMoEngageSDK(application: Application) {
        val moEngage = MoEngage.Builder(
            application,
            BuildConfig.MOENGAGE_WORKSPACE_ID,
            DataCenter.DATA_CENTER_1
        ).apply {
            configureNotificationMetaData(
                NotificationConfig(
                    smallIcon = R.drawable.icon,
                    largeIcon = R.drawable.ic_launcher,
                    notificationColor = R.color.notificationColor,
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true
                )
            )
            configureFcm(FcmConfig(false))
            enablePartnerIntegration(IntegrationPartner.SEGMENT)
            configureLogs(LogConfig(LogLevel.VERBOSE, true))
        }.build()
        MoEngage.initialiseDefaultInstance(moEngage)

        // Setting CustomPushMessageListener for notification customisation
        MoEPushHelper.getInstance().registerMessageListener(CustomPushMessageListener())

        // InApp related callbacks
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(InAppLifecycleCallback())
        MoEInAppHelper.getInstance().setClickActionListener(InAppClickListener())
    }

    private fun configureSegmentSDK(application: Application) {
        analytics = Analytics(BuildConfig.SEGMENT_WRITE_KEY, this).apply {
            add(MoEngageDestination(application))
        }
        Analytics.debugLogsEnabled = true
    }
}
