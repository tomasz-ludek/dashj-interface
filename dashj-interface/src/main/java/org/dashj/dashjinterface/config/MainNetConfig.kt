package org.dashj.dashjinterface.config

class MainNetConfig : WalletConfig(
        _name = NAME,
        _network = Network.MAINNET,
        _filesPrefix = "mainnet",
        _checkpointsAssetPath = "mainnet.checkpoints") {

    companion object {
        const val NAME = "mainnet"
        @JvmStatic
        fun get() = MainNetConfig()
    }
}
