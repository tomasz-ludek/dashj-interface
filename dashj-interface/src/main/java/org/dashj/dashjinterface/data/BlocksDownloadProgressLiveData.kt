package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.*
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.dashj.dashjinterface.WalletAppKitService

open class BlocksDownloadProgressLiveData<T>(application: Application) :
        WalletAppKitServiceAsyncLiveData<T>(application), BlocksDownloadedEventListener {

    protected lateinit var peerGroup: PeerGroup
    protected lateinit var chain: BlockChain
    private var prevSyncProgress: Int = -1

    override fun onActive(walletAppKitService: WalletAppKitService) {
        peerGroup = walletAppKitService.peerGroup
        chain = walletAppKitService.chain
        peerGroup.addBlocksDownloadedEventListener(this)
        progress(0, -1)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        peerGroup.removeBlocksDownloadedEventListener(this)
    }

    override fun onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock?, blocksLeft: Int) {
        val chainHeadHeight = chain.chainHead.height
        val mostCommonChainHeight = if (blocksLeft > 0) peerGroup.mostCommonChainHeight else chainHeadHeight
        val progressPercentage = (chainHeadHeight.toFloat() / mostCommonChainHeight.toFloat() * 100).toInt()
        // limit the number of updates
        if (prevSyncProgress != progressPercentage) {
            progress(progressPercentage, blocksLeft)
        }
        prevSyncProgress = progressPercentage
    }

    open fun progress(progress: Int, blocksLeft: Int) {

    }
}
