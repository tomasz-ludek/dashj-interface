package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.Transaction
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletChangeEventListener
import org.dashj.dashjinterface.WalletAppKitService

class TransactionsLiveData(application: Application) :
        WalletAppKitServiceLiveData<TransactionsLiveData.Data>(application), WalletChangeEventListener {

    override fun onActive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.also {
            it.addChangeEventListener(this)
            updateData(it)
        }
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        walletAppKitService.wallet.removeChangeEventListener(this)
    }

    override fun onWalletChanged(wallet: Wallet) {
        updateData(wallet)
    }

    private fun updateData(wallet: Wallet) {
        postValue(Data(wallet.transactionsByTime, wallet))
    }

    class Data(
            private val _transactions: List<Transaction>,
            private val _wallet: Wallet) {

        val transactions
            get() = _transactions

        val wallet
            get() = _wallet
    }
}