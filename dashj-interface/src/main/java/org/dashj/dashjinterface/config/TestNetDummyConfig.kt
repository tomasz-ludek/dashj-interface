package org.dashj.dashjinterface.config

class TestNetDummyConfig : WalletConfig(
        _name = NAME,
        _network = Network.TESTNET,
        _filesPrefix = "testnet-dummy",
        _checkpointsAssetPath = "testnet.checkpoints",
        _seed = arrayListOf("erode", "bridge", "organ", "you", "often", "teach", "desert", "thrive", "spike", "pottery", "sight", "sport")) {

    companion object {
        const val NAME = "dummy (testnet)"
        @JvmStatic
        fun get() = TestNetDummyConfig()
    }
}
