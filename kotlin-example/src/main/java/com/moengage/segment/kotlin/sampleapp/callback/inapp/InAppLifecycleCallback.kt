package com.moengage.segment.kotlin.sampleapp.callback.inapp

import android.util.Log
import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.model.InAppData

class InAppLifecycleCallback : InAppLifeCycleListener {
    override fun onDismiss(inAppData: InAppData) {
        Log.i("InAppLifecycleCallback", "InApp Dismissed callback: $inAppData")
    }

    override fun onShown(inAppData: InAppData) {
        Log.i("InAppLifecycleCallback", "InApp Shown callback: $inAppData")
    }
}
