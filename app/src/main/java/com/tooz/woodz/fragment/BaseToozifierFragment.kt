package com.tooz.woodz.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.tooz.woodz.R
import com.tooz.woodz.WoodzApplication
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener

open class BaseToozifierFragment : Fragment() {

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val WATCH_EVENT = "Watch event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }

    private lateinit var promptView: View

    protected val toozifier = WoodzApplication.getToozApplication().toozifier

    fun registerToozer() {
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            setUpUi()
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        }
    }

    private fun setUpUi(){
        promptView = layoutInflater.inflate(R.layout.layout_prompt, null)

        toozifier.updateCard(
            promptView = promptView,
            focusView = promptView,
            timeToLive = Constants.FRAME_TIME_TO_LIVE_FOREVER
        )
    }

    fun deregisterToozer() {
        toozifier.deregister()
    }

    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
    }
}