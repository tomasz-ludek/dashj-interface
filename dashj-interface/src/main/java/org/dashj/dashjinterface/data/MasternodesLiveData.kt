package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManagerListener
import org.dashj.dashjinterface.WalletAppKitService

class MasternodesLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<Masternode>>(application), MasternodeManagerListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeManager.addEventListener(this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeManager.removeEventListener(this)
    }

    override fun onMasternodeCountChanged(newCount: Int) {
        postValue(walletAppKitService!!.wallet.context.masternodeManager.masternodes)
    }
}
