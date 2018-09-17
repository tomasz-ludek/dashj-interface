package org.dash.dashwalletkit.data

import android.app.Application
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener
import org.dash.dashwalletkit.WalletAppKitService

class NewTransactionLiveData(application: Application) :
        WalletAppKitServiceLiveData<Transaction>(application), WalletCoinsReceivedEventListener, WalletCoinsSentEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.addCoinsReceivedEventListener(Threading.SAME_THREAD, this)
        walletAppKitService.wallet.addCoinsSentEventListener(Threading.SAME_THREAD, this)
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