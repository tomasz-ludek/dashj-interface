package org.dash.dashjinterface

import android.app.Application
import org.dash.dashwalletkit.WalletAppKitService

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WalletAppKitService.init(this)
    }
}
