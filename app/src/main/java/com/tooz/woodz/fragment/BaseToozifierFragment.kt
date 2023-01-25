package com.tooz.woodz.fragment

import androidx.fragment.app.Fragment
import com.tooz.woodz.WoodzApplication

open class BaseToozifierFragment : Fragment() {

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val WATCH_EVENT = "Watch event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }

    protected val toozifier = WoodzApplication.getToozApplication().toozifier

    fun deregisterToozer() {
        toozifier.deregister()
    }
}