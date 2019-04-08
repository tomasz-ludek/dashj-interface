package org.dashj.dashjinterface.config

import android.content.ContextWrapper
import android.os.Parcel
import android.os.Parcelable
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.DevNetParams
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.RegTestParams
import org.bitcoinj.params.TestNet3Params
import java.io.IOException
import java.io.InputStream

open class WalletConfig(
        private val _name: String,
        private val _network: Network,
        private val _filesPrefix: String,
        private val _checkpointsAssetPath: String? = null,
        private val _seed: List<String>? = null) : Parcelable {

    val name
        get() = _name

    val network
        get() = _network

    val filesPrefix
        get() = _filesPrefix

    val checkpointsAssetPath
        get() = _checkpointsAssetPath

    val seed
        get() = _seed

    val seedBased
        get() = (_seed != null)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            Network.valueOf(parcel.readString()),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_name)
        parcel.writeString(_network.name)
        parcel.writeString(_filesPrefix)
        parcel.writeString(_checkpointsAssetPath)
        parcel.writeStringList(_seed)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<WalletConfig> {
            override fun createFromParcel(parcel: Parcel): WalletConfig {
                return WalletConfig(parcel)
            }

            override fun newArray(size: Int): Array<WalletConfig?> {
                return arrayOfNulls(size)
            }
        }
    }

    val networkParams: NetworkParameters
        get() = when (_network) {
            Network.MAINNET -> MainNetParams.get()
            Network.TESTNET -> TestNet3Params.get()
            Network.REGNET -> RegTestParams.get()
            Network.DEVNET_MAITHAI -> {
                val dnsSeeds = arrayOf("devnet-maithai.thephez.com",
                        "54.187.113.35", "54.200.201.200", "34.216.233.163",
                        "34.221.188.185", "54.189.63.67", "52.40.117.135",
                        "54.187.111.107", "34.212.68.164", "18.237.142.23",
                        "54.202.73.177")
                DevNetParams.get("maithai", "yMtULrhoxd8vRZrsnFobWgRTidtjg2Rnjm", 20001, dnsSeeds)
            }
        }

    fun getCheckpoints(context: ContextWrapper): InputStream? {
        return _checkpointsAssetPath?.let {
            try {
                context.assets.open(_checkpointsAssetPath)
            } catch (e: IOException) {
                throw IllegalStateException(e.message, e)
            }
        }
    }

    enum class Network {
        MAINNET,
        TESTNET,
        DEVNET_MAITHAI,
        REGNET
    }
}
