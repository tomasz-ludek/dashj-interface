package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.SporkManager
import org.bitcoinj.core.SporkMessage
import org.bitcoinj.core.listeners.SporkManagerListener
import org.dashj.dashjinterface.WalletAppKitService

class SporksLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<SporksLiveData.Data>(application), SporkManagerListener {

    private lateinit var sporkManager: SporkManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        sporkManager = walletAppKitService.wallet.context.sporkManager
        sporkManager.addEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        sporkManager.removeEventListener(this)
    }

    override fun onUpdate(sporkMessage: SporkMessage?) {
        updateData()
    }

    private fun updateData() {
        postValueAsync { loadData() }
    }

    private fun loadData(): Data {
        val sporks = sporkManager.sporks.sortedWith(compareBy { it.sporkID })
        val active = mutableSetOf<SporkMessage>()
        for (spork in sporks) {
            if (sporkManager.isSporkActive(spork.sporkID)) {
                active.add(spork)
            }
        }
        return Data(sporks, active)
    }

    class Data(
            private val _sporks: List<SporkMessage>,
            private val _active: Set<SporkMessage>) {

        val sporks
            get() = _sporks

        val active
            get() = _active
    }
}
