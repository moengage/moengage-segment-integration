package com.moengage.segment.example.callbacks.inapp

import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.model.InAppData
import timber.log.Timber

/**
 * @author Umang Chamaria
 * Date: 2021/12/31
 */
class InAppLifecycleCallback: InAppLifeCycleListener {
    override fun onDismiss(inAppData: InAppData) {
        Timber.v("InApp Dismissed callback: $inAppData")
    }

    override fun onShown(inAppData: InAppData) {
        Timber.v("InApp Shown callback: $inAppData")
    }
}