package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.MasternodeManagerListener
import org.bitcoinj.utils.Threading
import org.dash.dashwalletkit.WalletAppKitService

class MasternodesLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<Masternode>>(application), MasternodeManagerListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeManager.addEventListener(this, Threading.SAME_THREAD)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeManager.removeEventListener(this)
    }

    override fun onMasternodeCountChanged(newCount: Int) {
        postValue(walletAppKitService!!.wallet.context.masternodeManager.masternodes)
    }
}
