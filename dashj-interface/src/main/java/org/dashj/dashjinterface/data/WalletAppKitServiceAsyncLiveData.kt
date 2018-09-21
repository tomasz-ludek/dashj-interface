package org.dashj.dashjinterface.data

import android.app.Application
import android.os.Handler
import android.os.HandlerThread

open class WalletAppKitServiceAsyncLiveData<T>(private val application: Application)
    : WalletAppKitServiceLiveData<T>(application) {

    private lateinit var valueUpdateThread: HandlerThread
    private lateinit var valueUpdateHandler: Handler

    override fun onActive() {
        super.onActive()
        valueUpdateThread = HandlerThread(this.javaClass.simpleName)
        valueUpdateThread.start()
        valueUpdateHandler = Handler(valueUpdateThread.looper)
    }

    override fun onInactive() {
        super.onInactive()
        valueUpdateHandler.removeCallbacksAndMessages(null)
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
