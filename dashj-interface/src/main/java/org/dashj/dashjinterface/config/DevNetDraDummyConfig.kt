package org.dashj.dashjinterface.config

class DevNetDraDummyConfig : WalletConfig(
        _name = NAME,
        _network = Network.DEVNET_DRA,
        _filesPrefix = "devnet-dra-dummy",
        _checkpointsAssetPath = null,
        _seed = arrayListOf("erode", "bridge", "organ", "you", "often", "teach", "desert", "thrive", "spike", "pottery", "sight", "sport")) {

    companion object {
        const val NAME = "dummy (devnet-DRA)"
        fun get() = DevNetDraDummyConfig()
    }
}
