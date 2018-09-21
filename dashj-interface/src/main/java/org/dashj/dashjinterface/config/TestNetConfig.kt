package org.dashj.dashjinterface.config

class TestNetConfig : WalletConfig(
        _name = NAME,
        _network = Network.TESTNET,
        _filesPrefix = "testnet",
        _checkpointsAssetPath = "testnet.checkpoints") {

    companion object {
        const val NAME = "testnet"
        @JvmStatic
        fun get() = TestNetConfig()
    }
}
