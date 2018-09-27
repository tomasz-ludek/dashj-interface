package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.core.listeners.EvolutionUserManagerListener
import org.bitcoinj.evolution.EvolutionUser
import org.bitcoinj.evolution.EvolutionUserManager
import org.dashj.dashjinterface.WalletAppKitService

class EvoUsersLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<List<EvolutionUser>>(application), EvolutionUserManagerListener {

    private lateinit var evolutionUserManager: EvolutionUserManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        evolutionUserManager = walletAppKitService.wallet.context.evoUserManager
        evolutionUserManager.addEventListener(this)
        updateValue()
    }

    override fun onInactive(walletAppKitService: WalletAppKitService) {
        evolutionUserManager.removeEventListener(this)
    }

    override fun onUserRemoved(user: EvolutionUser?) {
        updateValue()
    }

    override fun onUserAdded(user: EvolutionUser?) {
        updateValue()
    }

    private fun updateValue() {
        postValueAsync { evolutionUserManager.users }
    }
}
