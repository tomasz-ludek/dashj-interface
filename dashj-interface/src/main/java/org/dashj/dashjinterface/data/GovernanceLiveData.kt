package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.governance.listeners.GovernanceManagerListener
import org.dashj.dashjinterface.WalletAppKitService

class GovernanceLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<GovernanceObject>>(application), GovernanceManagerListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.governanceManager.addEventListener(this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.governanceManager.removeEventListener(this)
    }

    override fun onGovernanceObjectAdded(nHash: Sha256Hash, governanceObject: GovernanceObject) {
        val governanceObjects = walletAppKitService!!.wallet.context.governanceManager.getAllNewerThan(0)
        postValue(governanceObjects)
    }
}
