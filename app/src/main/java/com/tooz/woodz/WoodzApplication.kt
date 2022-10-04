package com.tooz.woodz

import android.app.Application
import com.tooz.woodz.database.AppDatabase
import tooz.bto.toozifier.ToozifierFactory


class WoodzApplication: Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    companion object {
        private lateinit var instance: WoodzApplication
        fun getToozApplication(): WoodzApplication = instance
    }

    val toozifier = ToozifierFactory.getInstance()

    init {
        instance = this
    }
}