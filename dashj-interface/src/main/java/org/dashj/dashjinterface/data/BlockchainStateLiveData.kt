package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener

class BlockchainStateLiveData(application: Application) :
        BlocksDownloadProgressLiveData<BlockchainState>(application), BlocksDownloadedEventListener {

    override fun progress(progress: Int, blocksLeft: Int) {
        postValue(walletAppKitService!!.blockchainState(blocksLeft))
    }
}
