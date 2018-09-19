package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Peer
import org.bitcoinj.core.listeners.PeerConnectedEventListener
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener
import org.dashj.dashjinterface.WalletAppKitService

class PeerConnectivityLiveData(application: Application) :
        WalletAppKitServiceLiveData<List<Peer>>(application), PeerConnectedEventListener, PeerDisconnectedEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.peerGroup.addConnectedEventListener(this)
        walletAppKitService.peerGroup.addDisconnectedEventListener(this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.peerGroup.removeConnectedEventListener(this)
        walletAppKitService.peerGroup.removeDisconnectedEventListener(this)
    }

    override fun onPeerConnected(peer: Peer, peerCount: Int) {
        postValue(walletAppKitService!!.peerGroup.connectedPeers)
    }

    override fun onPeerDisconnected(peer: Peer, peerCount: Int) {
        postValue(walletAppKitService!!.peerGroup.connectedPeers)
    }
}
