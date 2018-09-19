package org.dashj.dashjinterface

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import org.bitcoinj.core.*
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.Wallet
import org.dashj.dashjinterface.config.KitConfigTestnet
import org.dashj.dashjinterface.config.WalletAppKitConfig
import org.dashj.dashjinterface.data.BlockchainState
import org.dashj.dashjinterface.util.MainPreferences
import org.dashj.dashjinterface.util.NotificationAgent
import java.util.concurrent.Executor


class WalletAppKitService : Service() {

    companion object {
        private val TAG = WalletAppKitService::class.java.canonicalName
        private const val WALLET_APP_KIT_DIR = "walletappkit"
        private const val MIN_BROADCAST_CONNECTIONS = 2
        private const val MAX_CONNECTIONS = 14

        fun init(context: Context) {
            val walletAppKitServiceIntent = Intent(context, WalletAppKitService::class.java)
            ContextCompat.startForegroundService(context, walletAppKitServiceIntent)
        }
    }

    private lateinit var kit: WalletAppKit
    private lateinit var kitConfig: WalletAppKitConfig
    private lateinit var preferences: MainPreferences
    private lateinit var notificationAgent: NotificationAgent

    private val mBinder = LocalBinder()
    private var isSetupComplete = false
    private val onSetupCompleteListeners = mutableListOf<OnSetupCompleteListener>()

    private val mainThreadHandler = Handler()

    val blockchainState: BlockchainState?
        get() = kit.chain()?.let {
            val chainHead = kit.chain().chainHead
            val bestChainDate = chainHead.header.time
            val bestChainHeight = chainHead.height
            val blocksLeft = kit.peerGroup().mostCommonChainHeight - chainHead.height

            return BlockchainState(bestChainDate, bestChainHeight, blocksLeft)
        }

    val wallet: Wallet
        get() = kit.wallet()

    val peerGroup: PeerGroup
        get() = kit.peerGroup()

    inner class LocalBinder : Binder() {
        val service: WalletAppKitService
            get() = this@WalletAppKitService
    }

    override fun onCreate() {
        super.onCreate()

        Threading.USER_THREAD = Executor {
            mainThreadHandler.post(it)
        }

        kitConfig = KitConfigTestnet()

        preferences = MainPreferences(applicationContext)
        notificationAgent = NotificationAgent(this)
        startForeground(NotificationAgent.SYNC_NOTIFICATION_ID, notificationAgent.syncNotification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        initWalletAppKit()
        return Service.START_NOT_STICKY
    }

    private fun initWalletAppKit() {
        val walletAppKitDir = application.getDir(WALLET_APP_KIT_DIR, Context.MODE_PRIVATE)
        kit = object : WalletAppKit(kitConfig.networkParams, walletAppKitDir, kitConfig.filesPrefix, false) {
            override fun onSetupCompleted() {
                this@WalletAppKitService.onSetupCompleted()
            }
        }
        kit.setAutoSave(true)
        kit.startAsync()
    }

    private fun onSetupCompleted() {
        isSetupComplete = true

        peerGroup.minBroadcastConnections = MIN_BROADCAST_CONNECTIONS
        peerGroup.maxConnections = MAX_CONNECTIONS

        wallet.let {
            if (it.keyChainGroupSize < 1) {
                it.importKey(ECKey())
            }
        }
        wallet.context.masternodeSync.addEventListener { newStatus, _ ->
            if (newStatus == MasternodeSync.MASTERNODE_SYNC_FINISHED) {
                preferences.fullSyncDate = System.currentTimeMillis()
                stopForeground(true)
                stopSelf()
            }
        }
//        kit.setDownloadListener(object : DownloadProgressTracker() {
//            override fun progress(pct: Double, blocksSoFar: Int, date: Date) {
//                updateSyncNotification(date.toString(), pct.toInt())
//            }
//        })
        wallet.context.peerGroup.addBlocksDownloadedEventListener { peer, block, filteredBlock, blocksLeft ->
            val chainHeadHeight = kit.chain().chainHead.height
            val mostCommonChainHeight = kit.peerGroup().mostCommonChainHeight
            notificationAgent.updateSyncProgress(this, mostCommonChainHeight, chainHeadHeight)
        }

        kitConfig.getCheckpoints(this)?.let {
            kit.setCheckpoints(it)
        }

        notifyOnSetupCompletedListeners()
    }

    fun sendFunds(address: String, amount: Coin, result: Result<Transaction>) {
        val targetAddress = Address.fromBase58(wallet.networkParameters, address)
        try {
            val sendCoinsResult = wallet.sendCoins(peerGroup, targetAddress, amount)
            sendCoinsResult.broadcastComplete.addListener(Runnable {
                result.onSuccess(sendCoinsResult.tx)
            }, Threading.USER_THREAD)
        } catch (ex: Exception) {
            result.onFailure(ex)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    fun registerListener(listener: OnSetupCompleteListener) {
        onSetupCompleteListeners.add(listener)
        if (isSetupComplete) {
            listener.onServiceSetupComplete()
        }
    }

    fun unregisterListener(listener: OnSetupCompleteListener) {
        onSetupCompleteListeners.remove(listener)
    }

    private fun notifyOnSetupCompletedListeners() {
        mainThreadHandler.post {
            onSetupCompleteListeners.forEach {
                it.onServiceSetupComplete()
            }
        }
    }

    override fun onDestroy() {
        onSetupCompleteListeners.clear()
        kit.stopAsync()
        super.onDestroy()
    }

    interface OnSetupCompleteListener {
        fun onServiceSetupComplete()
    }

    interface Result<T> {
        fun onSuccess(result: T)
        fun onFailure(ex: Exception)
    }
}
