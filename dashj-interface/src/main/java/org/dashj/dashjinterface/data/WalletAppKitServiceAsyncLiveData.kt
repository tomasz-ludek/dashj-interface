package org.dashj.dashjinterface.data

import android.app.Application
import android.os.Handler
import android.os.HandlerThread

open class WalletAppKitServiceAsyncLiveData<T>(private val application: Application)
    : WalletAppKitServiceLiveData<T>(application) {

    private var valueUpdateThread = HandlerThread(javaClass::getSimpleName.name)
    private lateinit var valueUpdateHandler: Handler

    override fun onActive() {
        super.onActive()
        valueUpdateThread.start()
        valueUpdateHandler = Handler(valueUpdateThread.looper)
    }

    override fun onInactive() {
        super.onInactive()
        valueUpdateThread.quit()
    }

    protected fun postValueAsync(loadData: () -> T) {
        valueUpdateHandler.removeCallbacksAndMessages(null)
        valueUpdateHandler.post {
            val value = loadData()
            postValue(value)
        }
    }
}
