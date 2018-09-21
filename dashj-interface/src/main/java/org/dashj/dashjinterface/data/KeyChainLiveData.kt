package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.ECKey
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.KeyChainEventListener
import org.bitcoinj.wallet.listeners.WalletChangeEventListener
import org.dashj.dashjinterface.WalletAppKitService
import org.dashj.dashjinterface.util.RestrictedAccessUtil

class KeyChainLiveData(application: Application) :
        WalletAppKitServiceLiveData<KeyChainLiveData.Data>(application),
        KeyChainEventListener, WalletChangeEventListener {

    private lateinit var wallet: Wallet

    override fun onActive(walletAppKitService: WalletAppKitService) {
        wallet = walletAppKitService.wallet
        wallet.addKeyChainEventListener(this)
        wallet.addChangeEventListener(this)
        updateData()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        wallet.removeKeyChainEventListener(this)
        wallet.removeChangeEventListener(this)
    }

    override fun onKeysAdded(keys: MutableList<ECKey>?) {
        updateData()
    }

    override fun onWalletChanged(wallet: Wallet) {
        updateData()
    }

    private fun updateData() {
        wallet.run {
            val keyChainGroup = activeKeyChain
            val keyList = RestrictedAccessUtil.invokeGetKeys(keyChainGroup, false)
            val addressList = mutableListOf<ECKey>()
            addressList.addAll(keyList)
            val importedKeys = importedKeys
            addressList.addAll(importedKeys)
            postValue(Data(addressList, wallet))
        }
    }

    class Data(
            private val _keys: List<ECKey>,
            private val _wallet: Wallet) {

        val keys
            get() = _keys

        val wallet
            get() = _wallet
    }
}
