package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.governance.GovernanceManager
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.governance.listeners.GovernanceManagerListener
import org.dashj.dashjinterface.WalletAppKitService

class GovernanceLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<List<GovernanceObject>>(application), GovernanceManagerListener {

    private lateinit var governanceManager: GovernanceManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        governanceManager = walletAppKitService.wallet.context.governanceManager
        governanceManager.addEventListener(this)
        updateDate()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        governanceManager.removeEventListener(this)
    }

    override fun onGovernanceObjectAdded(nHash: Sha256Hash, governanceObject: GovernanceObject) {
        updateDate()
    }

    private fun updateDate() {
        postValueAsync { governanceManager.getAllNewerThan(0) }
    }
}
