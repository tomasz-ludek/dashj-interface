package org.dashj.dashjinterface

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WalletAppKitService.init(this)
    }
}
