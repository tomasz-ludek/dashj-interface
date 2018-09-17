package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.Block
import org.bitcoinj.core.FilteredBlock
import org.bitcoinj.core.Peer
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.bitcoinj.utils.Threading
import org.dash.dashwalletkit.WalletAppKitService

class BlockchainStateLiveData(application: Application) :
        WalletAppKitServiceLiveData<BlockchainState>(application), BlocksDownloadedEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.peerGroup.addBlocksDownloadedEventListener(Threading.SAME_THREAD, this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.peerGroup.removeBlocksDownloadedEventListener(this)
    }

    override fun onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock?, blocksLeft: Int) {
        postValue(walletAppKitService!!.blockchainState)
    }
}
