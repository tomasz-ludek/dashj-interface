package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.*
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.bitcoinj.store.BlockStore
import org.bitcoinj.store.BlockStoreException
import org.bitcoinj.wallet.Wallet
import org.dashj.dashjinterface.WalletAppKitService

class BlockchainSyncLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<BlockchainSyncLiveData.Data>(application), BlocksDownloadedEventListener {

    private lateinit var peerGroup: PeerGroup
    private lateinit var chain: BlockChain
    private lateinit var store: BlockStore
    private lateinit var wallet: Wallet
    private var prevSyncProgress: Int = -1

    override fun onActive(walletAppKitService: WalletAppKitService) {
        peerGroup = walletAppKitService.peerGroup
        chain = walletAppKitService.chain
        store = walletAppKitService.store
        wallet = walletAppKitService.wallet
        peerGroup.addBlocksDownloadedEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        peerGroup.removeBlocksDownloadedEventListener(this)
    }

    override fun onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock?, blocksLeft: Int) {
        val chainHeadHeight = chain.chainHead.height
        val mostCommonChainHeight = peerGroup.mostCommonChainHeight
        val progressPercentage = (chainHeadHeight.toFloat() / mostCommonChainHeight.toFloat() * 100).toInt()
        // limit the number of updates
        if (prevSyncProgress != progressPercentage || chainHeadHeight == mostCommonChainHeight) {
            updateData()
        }
        prevSyncProgress = progressPercentage
    }

    private fun updateData() {
        postValueAsync { loadData() }
    }

    private fun loadData(): BlockchainSyncLiveData.Data {
        val maxBlocks = 1024
        val blocks = mutableListOf<StoredBlock>()
        try {
            var block = chain.chainHead
            while (block != null) {
                blocks.add(block)
                if (blocks.size >= maxBlocks) {
                    break
                }
                block = block.getPrev(store)
            }
        } catch (ignored: BlockStoreException) {
            // ignore
        }
        return Data(blocks, wallet)
    }

    class Data(
            private val _blocks: List<StoredBlock>,
            private val _wallet: Wallet) {

        val blocks
            get() = _blocks

        val wallet
            get() = _wallet
    }
}
