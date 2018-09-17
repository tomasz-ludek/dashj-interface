package org.dash.dashwalletkit.config

import android.content.ContextWrapper

import org.bitcoinj.core.NetworkParameters

import java.io.InputStream

interface WalletAppKitConfig {

    val networkParams: NetworkParameters

    val filesPrefix: String

    fun getCheckpoints(context: ContextWrapper): InputStream?
}
