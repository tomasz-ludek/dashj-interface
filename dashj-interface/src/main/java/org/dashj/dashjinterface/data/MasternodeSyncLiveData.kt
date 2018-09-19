package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.MasternodeSync
import org.bitcoinj.core.MasternodeSyncListener
import org.dashj.dashjinterface.WalletAppKitService

class MasternodeSyncLiveData(application: Application)
    : WalletAppKitServiceLiveData<Pair<MasternodeSyncLiveData.MasternodeSyncStatus, Double>>(application), MasternodeSyncListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeSync.addEventListener(this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeSync.removeEventListener(this)
    }

    override fun onSyncStatusChanged(newStatus: Int, syncStatus: Double) {
        val status = MasternodeSyncStatus.valueOf(newStatus)
        postValue(Pair(status, syncStatus))
    }

    enum class MasternodeSyncStatus {

        MASTERNODE_SYNC_FAILED,
        MASTERNODE_SYNC_INITIAL, // sync just started, was reset recently or still in IDB
        MASTERNODE_SYNC_WAITING, // waiting after initial to see if we can get more headers/blocks
        MASTERNODE_SYNC_LIST,
        MASTERNODE_SYNC_MNW,
        MASTERNODE_SYNC_GOVERNANCE,
        MASTERNODE_SYNC_GOVOBJ,
        MASTERNODE_SYNC_GOVOBJ_VOTE,
        MASTERNODE_SYNC_FINISHED;


        companion object {

            fun valueOf(value: Int) = when (value) {
                MasternodeSync.MASTERNODE_SYNC_FAILED -> MASTERNODE_SYNC_FAILED
                MasternodeSync.MASTERNODE_SYNC_INITIAL -> MASTERNODE_SYNC_INITIAL
                MasternodeSync.MASTERNODE_SYNC_WAITING -> MASTERNODE_SYNC_WAITING
                MasternodeSync.MASTERNODE_SYNC_LIST -> MASTERNODE_SYNC_LIST
                MasternodeSync.MASTERNODE_SYNC_MNW -> MASTERNODE_SYNC_MNW
                MasternodeSync.MASTERNODE_SYNC_GOVERNANCE -> MASTERNODE_SYNC_GOVERNANCE
                MasternodeSync.MASTERNODE_SYNC_GOVOBJ -> MASTERNODE_SYNC_GOVOBJ
                MasternodeSync.MASTERNODE_SYNC_GOVOBJ_VOTE -> MASTERNODE_SYNC_GOVOBJ_VOTE
                MasternodeSync.MASTERNODE_SYNC_FINISHED -> MASTERNODE_SYNC_FINISHED
                else -> throw IllegalArgumentException("Unsupported sync status $value")
            }
        }
    }
}
