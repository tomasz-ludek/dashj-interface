package org.dashj.dashjinterface.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.main_fragment.view.*
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.dashj.dashjinterface.R
import org.dashj.dashjinterface.WalletAppKitService
import java.util.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var layoutView: View
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        layoutView = inflater.inflate(R.layout.main_fragment, container, false)
        return layoutView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.walletInfoLiveData.observe(this, Observer { walletInfo ->
            layoutView.message1.text = "networkParameters: ${walletInfo!!.networkParameters.javaClass.simpleName}\nbalance: ${walletInfo.balance.toFriendlyString()}\naddress: ${walletInfo.currentReceiveAddress}\n"
            layoutView.message1.setOnClickListener {
                Log.d("currentReceiveAddress", "${walletInfo.currentReceiveAddress}")
            }
        })
        viewModel.peerConnectivity.observe(this, Observer { peerList ->
            layoutView.message2.text = "peers: ${peerList!!.size}\n${peerList}\n"
        })
        viewModel.blockchainState.observe(this, Observer { blockchainState ->
            blockchainState?.let {
                layoutView.message3.text = "bestChainDate: ${DateUtil.format(it.bestChainDate)}\nbestChainHeight: ${it.bestChainHeight}\nblocksLeft: ${it.blocksLeft}\n"
            }
        })
        viewModel.masternodeSync.observe(this, Observer { data ->
            val status = data!!.first.name.replace("MASTERNODE", "MN")
            layoutView.message4.text = "${layoutView.message4.text}${DateUtil.format(Date())} $status\n"
        })
        viewModel.masternodes.observe(this, Observer { masternodes ->
            masternodes?.let {
                layoutView.message5.text = "masternodes: ${it.size}"//\n$masternodes\n"
            }
        })
        viewModel.governanceObjects.observe(this, Observer { governanceObjects ->
            governanceObjects?.let {
                layoutView.message6.text = "governanceObjects: ${it.size}\n"//\governanceObjects\n"
            }
        })
        layoutView.button.setOnClickListener {
            viewModel.sendFunds("yi8eWv9S3pmHo5ZLuQn6ftwik4AYN2WHd6", Coin.COIN,
                    object : WalletAppKitService.Result<Transaction> {

                        override fun onSuccess(tx: Transaction) {
                            layoutView.message7.text = "${layoutView.message7.text}\nSent: ${tx.hashAsString}"
                        }

                        override fun onFailure(ex: Exception) {
                            layoutView.message7.text = "${layoutView.message7.text}\nFailure: ${ex.message}"
                        }
                    }
            )
        }
        viewModel.djService.observe(this, Observer { djServiceLiveData ->
            Toast.makeText(activity, if (djServiceLiveData != null) "Connected" else "Disconnected", Toast.LENGTH_LONG).show()
        })
    }

}
