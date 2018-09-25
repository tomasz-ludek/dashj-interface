package org.dashj.dashjinterface

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import org.bitcoinj.core.*
import org.bitcoinj.core.listeners.BlocksDownloadedEventListener
import org.bitcoinj.evolution.SubTxRegister
import org.bitcoinj.kits.EvolutionWalletAppKit
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.store.BlockStore
import org.bitcoinj.utils.Threading
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.SendRequest
import org.bitcoinj.wallet.Wallet
import org.dashj.dashjinterface.config.TestNetConfig
import org.dashj.dashjinterface.config.WalletConfig
import org.dashj.dashjinterface.data.BlockchainState
import org.dashj.dashjinterface.util.MainPreferences
import org.dashj.dashjinterface.util.NotificationAgent
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean


class WalletAppKitService : Service() {

    companion object {
        private val TAG = WalletAppKitService::class.java.canonicalName

        private const val ACTION_START = "action_start"
        private const val ACTION_STOP = "action_stop"

        private const val EXTRA_WALLET_CONFIG = "extra_wallet_config"

        private const val MIN_BROADCAST_CONNECTIONS = 2
        private const val MAX_CONNECTIONS = 14
        private const val EARLIEST_HD_SEED_CREATION_TIME = 1427610960L

        private var deactivated = AtomicBoolean(false)

        @JvmStatic
        fun init(context: Context) {
            init(context, TestNetConfig.get())
        }

        @JvmStatic
        fun init(context: Context, walletConfig: WalletConfig) {
            val intent = Intent(context, WalletAppKitService::class.java)
            intent.action = ACTION_START
            intent.putExtra(EXTRA_WALLET_CONFIG, walletConfig)
            ContextCompat.startForegroundService(context, intent)
        }

        @JvmStatic
        fun stop(context: Context) {
            val intent = Intent(context, WalletAppKitService::class.java)
            intent.action = ACTION_STOP
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private lateinit var kit: WalletAppKit
    private lateinit var walletConfig: WalletConfig
    private lateinit var preferences: MainPreferences
    private lateinit var notificationAgent: NotificationAgent

    private val mBinder = LocalBinder()
    private var isSetupComplete = false
    private val onSetupCompleteListeners = mutableListOf<OnSetupCompleteListener>()

    private val mainThreadHandler = Handler()

    val blockchainState: BlockchainState
        get() = kit.chain().let {
            val chainHead = it.chainHead
            val bestChainDate = chainHead.header.time
            val bestChainHeight = chainHead.height
            val blocksLeft = kit.peerGroup().mostCommonChainHeight - chainHead.height

            return BlockchainState(bestChainDate, bestChainHeight, blocksLeft)
        }

    val chain: BlockChain
        get() = kit.chain()

    val store: BlockStore
        get() = kit.store()

    val wallet: Wallet
        get() = kit.wallet()

    val peerGroup: PeerGroup
        get() = kit.peerGroup()

    val walletName
        get() = walletConfig.name

    inner class LocalBinder : Binder() {
        val service: WalletAppKitService
            get() = this@WalletAppKitService
    }

    override fun onCreate() {
        super.onCreate()

        Threading.USER_THREAD = Executor {
            mainThreadHandler.post(it)
        }

        preferences = MainPreferences(applicationContext)
        notificationAgent = NotificationAgent(this)
        startForeground(NotificationAgent.SYNC_NOTIFICATION_ID, notificationAgent.syncNotification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent.action) {
            ACTION_START -> {
                walletConfig = intent.getParcelableExtra(EXTRA_WALLET_CONFIG)
                initWalletAppKit()
            }
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
            }
            else -> throw UnsupportedOperationException("Unsupported action ${intent.action}")
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun initWalletAppKit() {
        val walletAppKitDir = application.getDir(walletConfig.filesPrefix, Context.MODE_PRIVATE)
        kit = when {
            walletConfig.network == WalletConfig.Network.DEVNET_DRA -> {
                object : EvolutionWalletAppKit(walletConfig.networkParams, walletAppKitDir, walletConfig.filesPrefix, false) {
                    override fun onSetupCompleted() {
                        this@WalletAppKitService.onSetupCompleted()
                    }
                }
            }
            else -> {
                object : WalletAppKit(walletConfig.networkParams, walletAppKitDir, walletConfig.filesPrefix, false) {
                    override fun onSetupCompleted() {
                        this@WalletAppKitService.onSetupCompleted()
                    }
                }
            }
        }
        if (walletConfig.seedBased && walletAppKitDir.list().isEmpty()) {
            val deterministicSeed = DeterministicSeed(walletConfig.seed, null, "", EARLIEST_HD_SEED_CREATION_TIME)
            kit.restoreWalletFromSeed(deterministicSeed)
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
        wallet.context.masternodeSync.addEventListener(masternodeSyncListener)
        wallet.context.peerGroup.addBlocksDownloadedEventListener(blocksDownloadedEventListener)

        walletConfig.getCheckpoints(this)?.let {
            kit.setCheckpoints(it)
        }

        notifyOnSetupCompletedListeners()
    }

    private val blocksDownloadedEventListener = BlocksDownloadedEventListener { _, _, _, _ ->
        if (deactivated.get()) {
            return@BlocksDownloadedEventListener
        }
        val chainHeadHeight = kit.chain().chainHead.height
        val mostCommonChainHeight = kit.peerGroup().mostCommonChainHeight
        notificationAgent.updateSyncProgress(this, mostCommonChainHeight, chainHeadHeight)
    }

    private val masternodeSyncListener = MasternodeSyncListener { newStatus, _ ->
        if (deactivated.get()) {
            return@MasternodeSyncListener
        }
        if (newStatus == MasternodeSync.MASTERNODE_SYNC_FINISHED) {
            preferences.fullSyncDate = System.currentTimeMillis()
//                stopForeground(true)
//                stopSelf()
        }
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

    fun createUser(result: Result<String>) {
        try {
            val amount = Coin.parseCoin("0.001")
            val privKey = ECKey.fromPrivate(kit.wallet().activeKeyChain.getKeyByPath(EvolutionWalletAppKit.EVOLUTION_ACCOUNT_PATH, false).privKeyBytes)
            val subTxRegister = SubTxRegister(1, "dj-demo" + Random().nextInt() / 1000, privKey)
            val req = SendRequest.forSubTxRegister(kit.params(), subTxRegister, amount)

            val sendResult = kit.wallet().sendCoins(req)
            val currentUser = kit.wallet().context.evoUserManager.getUser(sendResult.tx.hash)

            sendResult.broadcastComplete.addListener(Runnable {
                result.onSuccess("SubTxRegister! Transaction hash is " + sendResult.tx.hashAsString)
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
        deactivated.set(true)
        onSetupCompleteListeners.clear()
        if (isSetupComplete) {
            wallet.context.peerGroup.removeBlocksDownloadedEventListener(blocksDownloadedEventListener)
            wallet.context.masternodeSync.removeEventListener(masternodeSyncListener)
        }
        kit.stopAsync()
    }

    interface OnSetupCompleteListener {
        fun onServiceSetupComplete()
    }

    interface Result<T> {
        fun onSuccess(result: T)
        fun onFailure(ex: Exception)
    }
}
