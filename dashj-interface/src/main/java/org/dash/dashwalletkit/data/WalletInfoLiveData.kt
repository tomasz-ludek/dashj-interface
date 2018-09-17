package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.Transaction
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener
import org.dash.dashwalletkit.WalletAppKitService

class WalletInfoLiveData(application: Application) :
        WalletAppKitServiceLiveData<WalletInfoLiveData.WalletInfo>(application), WalletCoinsReceivedEventListener, WalletCoinsSentEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        val wallet = walletAppKitService.wallet
        wallet.addCoinsReceivedEventListener(Threading.SAME_THREAD, this)
        wallet.addCoinsSentEventListener(Threading.SAME_THREAD, this)
        postValue(WalletInfo(wallet.balance, wallet.currentReceiveAddress(), wallet.networkParameters))
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        val wallet = walletAppKitService.wallet
        wallet.removeCoinsSentEventListener(this)
        wallet.removeCoinsReceivedEventListener(this)
    }

    override fun onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(WalletInfo(newBalance, wallet.currentReceiveAddress(), wallet.networkParameters))
    }

    override fun onCoinsSent(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(WalletInfo(newBalance, wallet.currentReceiveAddress(), wallet.networkParameters))
    }

    class WalletInfo(
            private val _balance: Coin,
            private val _currentReceiveAddress: Address,
            private val _networkParameters: NetworkParameters) {

        val balance: Coin
            get() = _balance

        val currentReceiveAddress: Address
            get() = _currentReceiveAddress

        val networkParameters: NetworkParameters
            get() = _networkParameters
    }
}
