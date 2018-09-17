package org.dash.dashwalletkit.config

import android.content.ContextWrapper
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.DevNetParams
import java.io.InputStream

class KitConfigDevnetDra : WalletAppKitConfig {

    override val networkParams: NetworkParameters
        get() {
            val dnsSeeds = arrayOf("54.255.164.83", "52.77.231.13", "13.250.14.191")
            return DevNetParams.get("DRA", "yPhZ6EKNntpLDZBovHd1xAYjfwYmrBMT5N", 12999, dnsSeeds)
        }

    override val filesPrefix: String
        get() = "devnet-DRA"

    override fun getCheckpoints(context: ContextWrapper): InputStream? {
        return null
    }
}
