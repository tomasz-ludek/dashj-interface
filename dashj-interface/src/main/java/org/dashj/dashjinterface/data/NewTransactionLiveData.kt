package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener
import org.dashj.dashjinterface.WalletAppKitService

class NewTransactionLiveData(application: Application) :
        WalletAppKitServiceLiveData<Transaction>(application), WalletCoinsReceivedEventListener, WalletCoinsSentEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.addCoinsReceivedEventListener(this)
        walletAppKitService.wallet.addCoinsSentEventListener(this)
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.removeCoinsSentEventListener(this)
        walletAppKitService.wallet.removeCoinsReceivedEventListener(this)
    }

    override fun onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(tx)
    }

    override fun onCoinsSent(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        postValue(tx)
    }
}