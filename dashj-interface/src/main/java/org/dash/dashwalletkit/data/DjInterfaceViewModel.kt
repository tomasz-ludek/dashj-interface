package org.dash.dashwalletkit.data

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.dash.dashwalletkit.WalletAppKitService

open class DjInterfaceViewModel(application: Application) : AndroidViewModel(application) {

    private val _djService = DjServiceLiveData(application)
    val djService: DjServiceLiveData
        get() = _djService

    public fun sendFunds(address: String, amount: Coin, result: WalletAppKitService.Result<Transaction>) {
        djService.value?.sendFunds(address, amount, result)
    }
}
