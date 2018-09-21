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
            Network.DEVNET_DRA -> {
                val dnsSeeds = arrayOf("54.255.164.83", "52.77.231.13", "13.250.14.191")
                DevNetParams.get("DRA", "yPhZ6EKNntpLDZBovHd1xAYjfwYmrBMT5N", 12999, dnsSeeds)
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
        DEVNET_DRA,
        REGNET
    }
}
