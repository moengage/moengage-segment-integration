package com.moengage.segment.example.callbacks.inapp

import com.moengage.inapp.listeners.OnClickActionListener
import com.moengage.inapp.model.ClickData
import timber.log.Timber

/**
 * @author Umang Chamaria
 * Date: 2022/02/03
 */
class InAppClickListener: OnClickActionListener {

    override fun onClick(clickData: ClickData): Boolean {
        Timber.v("InApp Click Callback: $clickData")
        // return true if the application wants to handle the click.
        return false
    }
}