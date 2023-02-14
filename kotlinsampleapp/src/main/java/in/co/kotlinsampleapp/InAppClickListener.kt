package `in`.co.kotlinsampleapp

import android.util.Log
import com.moengage.inapp.listeners.OnClickActionListener
import com.moengage.inapp.model.ClickData


class InAppClickListener : OnClickActionListener {

    override fun onClick(clickData: ClickData): Boolean {
        Log.i("InAppClickListener", "InApp Click Callback: $clickData")
        // return true if the application wants to handle the click.
        return false
    }
}