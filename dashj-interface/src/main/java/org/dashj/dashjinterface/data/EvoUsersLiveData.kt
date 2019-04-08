package org.dashj.dashjinterface.data

import android.app.Application
import org.bitcoinj.evolution.EvolutionUser
import org.bitcoinj.evolution.EvolutionUserManager
import org.bitcoinj.evolution.listeners.EvolutionUserAddedEventListener
import org.bitcoinj.evolution.listeners.EvolutionUserRemovedEventListener
import org.dashj.dashjinterface.WalletAppKitService

class EvoUsersLiveData(application: Application) :
        WalletAppKitServiceAsyncLiveData<List<EvolutionUser>>(application),
        EvolutionUserAddedEventListener, EvolutionUserRemovedEventListener {

    private lateinit var evolutionUserManager: EvolutionUserManager

    override fun onActive(walletAppKitService: WalletAppKitService) {
        evolutionUserManager = walletAppKitService.wallet.context.evoUserManager
        evolutionUserManager.addUserAddedListener(this)
        evolutionUserManager.addUserRemovedListener(this)
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
