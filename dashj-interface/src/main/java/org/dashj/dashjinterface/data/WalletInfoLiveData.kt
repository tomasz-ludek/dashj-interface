package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletChangeEventListener
import org.dashj.dashjinterface.WalletAppKitService

class WalletInfoLiveData(application: Application) :
        WalletAppKitServiceLiveData<WalletInfoLiveData.Data>(application), WalletChangeEventListener {

    private lateinit var wallet: Wallet

    override fun onActive(walletAppKitService: WalletAppKitService) {
        wallet = walletAppKitService.wallet
        wallet.addChangeEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        wallet.removeChangeEventListener(this)
    }

    override fun onWalletChanged(wallet: Wallet) {
        updateData()
    }

    private fun updateData() {
        postValue(Data(wallet.balance, wallet.currentReceiveAddress(), wallet.networkParameters))
    }

    class Data(
            private val _balance: Coin,
            private val _currentReceiveAddress: Address,
            private val _networkParameters: NetworkParameters) {

        val balance
            get() = _balance

        val currentReceiveAddress
            get() = _currentReceiveAddress

        val networkParameters
            get() = _networkParameters
    }
}
