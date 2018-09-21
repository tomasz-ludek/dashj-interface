package org.dashj.dashjinterface.config

class DevNetDraConfig : WalletConfig(
        _name = NAME,
        _network = Network.DEVNET_DRA,
        _filesPrefix = "devnet-dra") {

    companion object {
        const val NAME = "devnet-DRA"
        @JvmStatic
        fun get() = DevNetDraConfig()
    }
}
