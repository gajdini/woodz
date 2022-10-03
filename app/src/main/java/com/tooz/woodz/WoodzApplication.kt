package com.tooz.woodz

import android.app.Application
import com.tooz.woodz.database.AppDatabase


class WoodzApplication: Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}