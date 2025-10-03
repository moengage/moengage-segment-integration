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
        val moEngage =
            MoEngage.Builder(
                    application, BuildConfig.MOENGAGE_WORKSPACE_ID, DataCenter.DATA_CENTER_1)
                .apply {
                    configureNotificationMetaData(
                        NotificationConfig(
                            smallIcon = R.drawable.icon,
                            largeIcon = R.drawable.ic_launcher,
                            notificationColor = R.color.notificationColor,
                            isMultipleNotificationInDrawerEnabled = true,
                            isBuildingBackStackEnabled = true,
                            isLargeIconDisplayEnabled = true))
                    configureFcm(FcmConfig(false))
                    enablePartnerIntegration(IntegrationPartner.SEGMENT)
                    configureLogs(LogConfig(LogLevel.VERBOSE, true))
                }
                .build()
        MoEngage.initialiseDefaultInstance(moEngage)

        // Setting CustomPushMessageListener for notification customisation
        MoEPushHelper.getInstance().registerMessageListener(CustomPushMessageListener())

        // InApp related callbacks
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(InAppLifecycleCallback())
        MoEInAppHelper.getInstance().setClickActionListener(InAppClickListener())
    }

    private fun configureSegmentSDK(application: Application) {
        analytics =
            Analytics(BuildConfig.SEGMENT_WRITE_KEY, this).apply {
                add(MoEngageDestination(application))
            }
        Analytics.debugLogsEnabled = true
    }
}
