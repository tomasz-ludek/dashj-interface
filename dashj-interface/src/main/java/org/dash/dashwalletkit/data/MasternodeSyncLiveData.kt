package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.MasternodeSyncListener
import org.bitcoinj.utils.Threading
import org.dash.dashwalletkit.WalletAppKitService

class MasternodeSyncLiveData(application: Application)
    : WalletAppKitServiceLiveData<Pair<MasternodeSyncLiveData.MasternodeSyncStatus, Double>>(application), MasternodeSyncListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.context.masternodeSync.addEventListener(this, Threading.SAME_THREAD)
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
                -1 -> MASTERNODE_SYNC_FAILED
                0 -> MASTERNODE_SYNC_INITIAL
                1 -> MASTERNODE_SYNC_WAITING
                2 -> MASTERNODE_SYNC_LIST
                3 -> MASTERNODE_SYNC_MNW
                4 -> MASTERNODE_SYNC_GOVERNANCE
                10 -> MASTERNODE_SYNC_GOVOBJ
                11 -> MASTERNODE_SYNC_GOVOBJ_VOTE
                999 -> MASTERNODE_SYNC_FINISHED
                else -> throw IllegalArgumentException("Unsupported sync status $value")
            }
        }
    }
}
