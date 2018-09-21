package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManager
import org.bitcoinj.core.MasternodeManagerListener
import org.dashj.dashjinterface.WalletAppKitService

class MasternodesLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<List<Masternode>>(application), MasternodeManagerListener {

    private lateinit var masternodeManager: MasternodeManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        masternodeManager = walletAppKitService.wallet.context.masternodeManager
        masternodeManager.addEventListener(this)
        updateValue()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        masternodeManager.removeEventListener(this)
    }

    override fun onMasternodeCountChanged(newCount: Int) {
        updateValue()
    }

    private fun updateValue() {
        postValueAsync { masternodeManager.masternodes }
    }
}
