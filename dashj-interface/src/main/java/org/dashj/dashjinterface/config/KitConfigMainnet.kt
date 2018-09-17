package org.dashj.dashjinterface.config

import android.content.ContextWrapper

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.MainNetParams

import java.io.IOException
import java.io.InputStream

class KitConfigMainnet : WalletAppKitConfig {

    override val networkParams: NetworkParameters
        get() = MainNetParams.get()

    override val filesPrefix: String
        get() = "mainnet"

    override fun getCheckpoints(context: ContextWrapper): InputStream? {
        return try {
            context.assets.open("checkpoints-mainnet.txt")
        } catch (e: IOException) {
            null
        }
    }
}
