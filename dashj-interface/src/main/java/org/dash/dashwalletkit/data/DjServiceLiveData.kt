package org.dash.dashwalletkit.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.dash.dashwalletkit.WalletAppKitService

open class DjServiceLiveData(private val application: Application) : LiveData<WalletAppKitService>(), ServiceConnection {

    override fun onActive() {
        application.bindService(Intent(application, WalletAppKitService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onInactive() {
        application.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        value = (service as WalletAppKitService.LocalBinder).service
    }

    override fun onServiceDisconnected(name: ComponentName) {
        value = null
    }
}
