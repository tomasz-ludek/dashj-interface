package org.dashj.dashjinterface.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.dashj.dashjinterface.WalletAppKitService
import java.util.concurrent.atomic.AtomicBoolean

open class WalletAppKitServiceLiveData<T>(private val application: Application)
    : LiveData<T>(), ServiceConnection, WalletAppKitService.OnSetupCompleteListener {

    private var _walletAppKitService: WalletAppKitService? = null
    protected val walletAppKitService: WalletAppKitService?
        get() = _walletAppKitService

    private val wasCustomOnActiveCalled = AtomicBoolean(false)

    override fun onActive() {
        application.bindService(Intent(application, WalletAppKitService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onInactive() {
        _walletAppKitService?.let {
            if (wasCustomOnActiveCalled.get()) {
                onInactive(it)
                wasCustomOnActiveCalled.set(false)
            }
            it.unregisterListener(this)
        }
        application.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        _walletAppKitService = (service as WalletAppKitService.LocalBinder).service
        _walletAppKitService!!.registerListener(this)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        _walletAppKitService = null
    }

    override fun onServiceSetupComplete() {
        _walletAppKitService?.let {
            onActive(it)
            wasCustomOnActiveCalled.set(true)
        }
    }

    open fun onActive(walletAppKitService: WalletAppKitService) {}

    open fun onInactive(walletAppKitService: WalletAppKitService) {}
}
