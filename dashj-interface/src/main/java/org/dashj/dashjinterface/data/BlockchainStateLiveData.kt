package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Block
import org.bitcoinj.core.FilteredBlock
import org.bitcoinj.core.Peer
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.dashj.dashjinterface.WalletAppKitService

class BlockchainStateLiveData(application: Application) :
        WalletAppKitServiceLiveData<BlockchainState>(application), BlocksDownloadedEventListener {

    private lateinit var peerGroup: PeerGroup

    override fun onActive(walletAppKitService: WalletAppKitService) {
        peerGroup = walletAppKitService.peerGroup
        peerGroup.addBlocksDownloadedEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        peerGroup.removeBlocksDownloadedEventListener(this)
    }

    override fun onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock?, blocksLeft: Int) {
        updateData()
    }

    private fun updateData() {
        postValue(walletAppKitService!!.blockchainState)
    }
}
