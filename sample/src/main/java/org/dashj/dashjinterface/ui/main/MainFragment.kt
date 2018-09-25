package org.dashj.dashjinterface.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.main_fragment.view.*
import org.bitcoinj.core.Coin
import org.dashj.dashjinterface.R
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

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.walletInfoLiveData.observe(this, Observer {
            layoutView.message1.text =
                    "networkParameters: ${it!!.networkParameters.javaClass.simpleName}\n" +
                    "balance: ${it.balance.toFriendlyString()}\n" +
                    "address: ${it.currentReceiveAddress}\n"
            layoutView.message1.setOnClickListener { _ ->
                Log.d("currentReceiveAddress", "${it.currentReceiveAddress}")
            }
        })
        viewModel.peerConnectivity.observe(this, Observer {
            layoutView.message2.text =
                    "peers: ${it!!.size}\n" +
                    "${it}\n"
        })
        viewModel.blockchainState.observe(this, Observer {
            layoutView.message3.text =
                    "bestChainDate: ${DateUtil.format(it!!.bestChainDate)}\n" +
                    "bestChainHeight: ${it.bestChainHeight}\n" +
                    "blocksLeft: ${it.blocksLeft}\n"
        })
        viewModel.masternodeSync.observe(this, Observer {
            val status = it!!.first.name.replace("MASTERNODE", "MN")
            layoutView.message4.text =
                    "${layoutView.message4.text}${DateUtil.format(Date())} $status\n"
        })
        viewModel.masternodes.observe(this, Observer {
            it?.let { _ ->
                layoutView.message5.text = "masternodes: ${it.size}"//\n$masternodes\n"
            }
        })
        viewModel.governanceObjects.observe(this, Observer {
            it?.let { _ ->
                layoutView.message6.text = "governanceObjects: ${it.size}\n"//\governanceObjects\n"
            }
        })
        layoutView.button.setOnClickListener {
            viewModel.sendFunds1("yXw7UUSCFMNqhKcnhanQkzRehwFQpqFmTP", Coin.parseCoin("99"))
        }
        viewModel.showMessageAction.observe(this, Observer {
            val isErrorMessage = it!!.first
            val message = it.second
            if (isErrorMessage) {
                layoutView.message7.setTextColor(Color.RED)
                layoutView.message7.text = "${layoutView.message7.text}\nSent: $message"
            } else {
                layoutView.message7.setTextColor(Color.GREEN)
                layoutView.message7.text = "${layoutView.message7.text}\nFailure: $message"
            }
        })
        viewModel.djService.observe(this, Observer { djServiceLiveData ->
            Toast.makeText(activity, if (djServiceLiveData != null) "Connected" else "Disconnected", Toast.LENGTH_LONG).show()
        })
        layoutView.createUserButton.setOnClickListener {
            viewModel.createUser()
        }
    }

}
