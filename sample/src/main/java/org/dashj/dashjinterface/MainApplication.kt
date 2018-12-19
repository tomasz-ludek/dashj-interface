package org.dashj.dashjinterface

import android.app.Application
import org.dashj.dashjinterface.config.DevNetDraDummyConfig
import org.dashj.dashjinterface.config.TestNetDummyConfig

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WalletAppKitService.init(this, TestNetDummyConfig.get())
    }
}
