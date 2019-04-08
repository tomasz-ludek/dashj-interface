package org.dashj.dashjinterface.config

class DevNetDraConfig : WalletConfig(
        _name = NAME,
        _network = Network.DEVNET_MAITHAI,
        _filesPrefix = "devnet-maithai") {

    companion object {
        const val NAME = "devnet-MAITHAI"
        @JvmStatic
        fun get() = DevNetDraConfig()
    }
}
