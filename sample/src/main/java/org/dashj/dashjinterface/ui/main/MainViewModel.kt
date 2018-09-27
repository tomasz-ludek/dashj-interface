package org.dashj.dashjinterface.ui.main

import android.app.Application
import android.arch.lifecycle.LiveData
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Masternode
import org.bitcoinj.core.Transaction
import org.bitcoinj.governance.GovernanceObject
import org.dashj.dashjinterface.WalletAppKitService
import org.dashj.dashjinterface.data.*

class MainViewModel(application: Application) : DjInterfaceViewModel(application) {

    private val _peerConnectivity = PeerConnectivityLiveData(application)
    val peerConnectivity: PeerConnectivityLiveData
        get() = _peerConnectivity

    private val _walletInfoLiveData = WalletInfoLiveData(application)
    val walletInfoLiveData: WalletInfoLiveData
        get() = _walletInfoLiveData

    private val _blockchainState = BlockchainStateLiveData(application)
    val blockchainState: BlockchainStateLiveData
        get() = _blockchainState

    private val _masternodes = MasternodesLiveData(application)
    val masternodes: LiveData<List<Masternode>>
        get() = _masternodes

    private val _governanceObjects = GovernanceLiveData(application)
    val governanceObjects: LiveData<List<GovernanceObject>>
        get() = _governanceObjects

    private val _masternodeSync = MasternodeSyncLiveData(application)
    val masternodeSync: MasternodeSyncLiveData
        get() = _masternodeSync

    private val _showMessageAction = SingleLiveEvent<Pair<Boolean, String>>()
    val showMessageAction
        get() = _showMessageAction

    fun sendFunds1(address: String, amount: Coin) {
        djService.value?.sendFunds(address, amount, object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(result: Transaction) {
                _showMessageAction.call(Pair(false, result.hashAsString))
            }

            override fun onFailure(ex: Exception) {
                _showMessageAction.call(Pair(true, ex.message!!))
            }
        })
    }

    fun createUser() {
        djService.value?.createUser(object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(result: Transaction) {
                _showMessageAction.call(Pair(false, result.hashAsString))
            }

            override fun onFailure(ex: Exception) {
                _showMessageAction.call(Pair(true, ex.message!!))
            }
        })
    }
}
