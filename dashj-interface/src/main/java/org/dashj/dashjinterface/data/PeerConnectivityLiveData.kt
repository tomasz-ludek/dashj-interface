package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Peer
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.core.listeners.PeerConnectedEventListener
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener
import org.dashj.dashjinterface.WalletAppKitService

class PeerConnectivityLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<Peer>>(application), PeerConnectedEventListener, PeerDisconnectedEventListener {

    private lateinit var peerGroup: PeerGroup

    override fun onActive(walletAppKitService: WalletAppKitService) {
        peerGroup = walletAppKitService.peerGroup
        peerGroup.addConnectedEventListener(this)
        peerGroup.addDisconnectedEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        peerGroup.removeConnectedEventListener(this)
        peerGroup.removeDisconnectedEventListener(this)
    }

    override fun onPeerConnected(peer: Peer, peerCount: Int) {
        updateData()
    }

    override fun onPeerDisconnected(peer: Peer, peerCount: Int) {
        updateData()
    }

    private fun updateData() {
        postValue(peerGroup.connectedPeers)
    }
}
