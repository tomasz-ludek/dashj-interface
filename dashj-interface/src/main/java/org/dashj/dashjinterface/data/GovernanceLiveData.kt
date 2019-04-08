package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.governance.GovernanceManager
import org.bitcoinj.governance.GovernanceObject
import org.bitcoinj.governance.listeners.GovernanceObjectAddedEventListener
import org.dashj.dashjinterface.WalletAppKitService

class GovernanceLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<List<GovernanceObject>>(application), GovernanceObjectAddedEventListener {

    private lateinit var governanceManager: GovernanceManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        governanceManager = walletAppKitService.wallet.context.governanceManager
        governanceManager.addGovernanceObjectAddedListener(this)
        updateDate()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        governanceManager.removeGovernanceObjectAddedListener(this)
    }

    override fun onGovernanceObjectAdded(nHash: Sha256Hash, governanceObject: GovernanceObject) {
        updateDate()
    }

    private fun updateDate() {
        postValueAsync { governanceManager.getAllNewerThan(0) }
    }
}
